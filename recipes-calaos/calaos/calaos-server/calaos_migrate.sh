#!/bin/sh

set -e #stop if any error occurs
set -o nounset #stop if variable are uninitialised

tmpdir="/tmp/old_calaos"

# Run as root, of course.
if [ "$(id -u)" != "0" ]
then
    echo "Must be root to run this script."
    exit 1
fi

echo "============================================"
echo "        Calaos-OS Migration script"
echo "============================================"
echo

clean_temp()
{
    set +e
    #clean tempdir if needed
    mkdir -p $tmpdir
    umount $tmpdir > /dev/null 2>&1
    set -e
}

clean_temp

echo "[*] Searching for an old calaos installation..."
#search for an old calaos partition
cdrive=""
while IFS='' read -r line || [[ -n "$line" ]]
do
    drive=$(echo $line | cut -d' ' -f4)
    if [ "${drive:0:3}" == "ram" ]; then continue; fi
    if [ "${drive:0:4}" == "loop" ]; then continue; fi

    if [ ! -b /dev/$drive ]; then continue; fi

    set +e
    mount /dev/$drive $tmpdir > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        if [ -d $tmpdir/etc/calaos ]
        then
            cdrive=$drive
            break
        fi
    fi
    set -e
done < /proc/partitions

if [ x$cdrive == x ]
then
    echo "No calaos installation found! Cannot migrate."
    echo "Are you sure a calaos-os was installed?"
    echo "Aborting."
    exit 1
fi

echo
echo "A calaos-os installation was found on /dev/$cdrive"
echo "The migration will continue using this disk."
echo
read -p "Continue (y/[N])?" choice
case "$choice" in 
  y|Y ) echo "[*] Starting migration process...";;
  * ) echo "Aborted."; exit 1;;
esac

#stop running services before changing config files
systemctl stop calaos-server calaos-home haproxy olad knxd influxdb grafana

echo "[*] Mounting old system"
set +e
umount /dev/$drive > /dev/null 2>&1
set -e
mount /dev/$cdrive $tmpdir

echo "[*] Copy calaos configuration"
cp -R $tmpdir/etc/calaos /etc/
if [ -e $tmpdir/home/root/.cache/calaos ]
then
    cp -R $tmpdir/home/root/.cache/calaos /home/root/.cache/
fi

echo "[*] Copy systemd service files"
dir=$(pwd)
cd $tmpdir/etc/systemd/system/multi-user.target.wants/

beginswith() { case $2 in "$1"*) true;; *) false;; esac; }

is_service() {
    if [ "$1" == "calaos-home.service" ]; then return 0; fi
    if [ "$1" == "calaos-server.service" ]; then return 0; fi
    if beginswith usb-serial-touchscreen $1; then return 0; fi
    if [ "$1" == "olad.service" ]; then return 0; fi
    if [ "$1" == "knxd.service" ]; then return 0; fi
    if [ "$1" == "influxdb.service" ]; then return 0; fi
    if [ "$1" == "grafana.service" ]; then return 0; fi
    if beginswith zigbee2mqtt $1; then return 0; fi
    return 1
}

for s in *.service
do
    if ! is_service "$s"
    then
        continue
    fi

    if [ -e $tmpdir/etc/systemd/system/multi-user.target.wants/$s ]
    then
        if [ ! -e /etc/systemd/system/multi-user.target.wants/$s ]
        then
            #service is not enabled
            echo " - Enabling service: $s"
            systemctl enable $s
        fi
    else
        if [ -e /etc/systemd/system/multi-user.target.wants/$s ]
        then
            #service was disabled, but is enabled on new os
            echo " - Disabling service: $s"
            systemctl disable $s
        fi
    fi
done
cd $dir

echo "[*] Copy touchscreen calibration data"
dir=$(pwd)

is_xconf() {
    if [ "$1" == "10-evdev.conf" ]; then return 0; fi
    if [ "$1" == "10-quirks.conf" ]; then return 0; fi
    if [ "$1" == "50-synaptics.conf" ]; then return 0; fi
    return 1
}

cd $tmpdir/usr/share/X11/xorg.conf.d
for c in *
do
    if is_xconf "$c"
    then
        continue
    fi

    echo "  copying $c"
    test -d /usr/share/X11/xorg.conf.d && cp $tmpdir/usr/share/X11/xorg.conf.d/$c /usr/share/X11/xorg.conf.d/$c
done
cd $dir

echo "[*] Copy ssh keys and root password"
test -d $tmpdir/home/root/.ssh && cp -R $tmpdir/home/root/.ssh /home/root/
rootpw=$(cat $tmpdir/etc/shadow | grep "root:")
sed -i "s/^root:.*\$/$(echo $rootpw | sed -e 's/[]\/$*.^|[]/\\&/g')/g" /etc/shadow
test -d $tmpdir/etc/dropbear && cp -R $tmpdir/etc/dropbear /etc/

echo "[*] Setup timezone"
cp -P $tmpdir/etc/localtime /etc/localtime
systemctl stop ntpd
systemctl start ntpd

if [ -e $tmpdir/etc/locale.conf ]
then
    echo "[*] Setup locale"
    cp $tmpdir/etc/locale.conf /etc/locale.conf
    lc=$(cat $tmpdir/etc/locale.conf | cut -d= -f2)
    km=$(cat $tmpdir/etc/vconsole.conf | cut -d= -f2)
    xk=$(cat $tmpdir/etc/X11/xorg.conf.d/00-keyboard.conf | grep XkbLayout | cut -d\" -f4)

    localectl set-locale LANG="$lc"
    localectl set-keymap $km
    localectl set-x11-keymap $xk
fi

echo "[*] Install SSL certificate"
if [ -e $tmpdir/etc/ssl/haproxy/server.pem ]
then
    cp $tmpdir/etc/ssl/haproxy/server.pem /etc/ssl/haproxy/server.pem
elif [ -e $tmpdir/etc/lighttpd/server.pem ]
then
    cp $tmpdir/etc/lighttpd/server.pem /etc/ssl/haproxy/server.pem
fi

if [ -e $tmpdir/etc/default/eibnetmux ]
then
    echo "[*] Copy eibnetmux configuration"
    cp $tmpdir/etc/default/eibnetmux /etc/default/eibnetmux
fi

if [ -d $tmpdir/etc/ola ]
then
    echo "[*] Copy OLA configuration"
    cp -R $tmpdir/etc/ola /etc/
fi

echo "[*] Copy network configuration"
if [ "$(ls -A $tmpdir/var/lib/connman)" ]
then
    cp -R $tmpdir/var/lib/connman/* /var/lib/connman
fi
if [ "$(ls -A $tmpdir/etc/systemd/network)" ]
then
    cp -R $tmpdir/etc/systemd/network/* /etc/systemd/network/
fi

echo "[*] Copy knxd.conf"
if [ -e $tmpdir/etc/knxd.conf ]
then
    cp $tmpdir/etc/knxd.conf /etc/knxd.conf
fi

echo "[*] Copy zigbee2mqtt config"
if [ -e $tmpdir/etc/zigbee2mqtt/configuration.yaml ]
then
    cp $tmpdir/usr/lib/node_modules/zigbee2mqtt/data/configuration.yaml /usr/lib/node_modules/zigbee2mqtt/data/configuration.yaml
    cp $tmpdir/usr/lib/node_modules/zigbee2mqtt/data/database.db /usr/lib/node_modules/zigbee2mqtt/data/database.db
    cp $tmpdir/usr/lib/node_modules/zigbee2mqtt/data/state.json /usr/lib/node_modules/zigbee2mqtt/data/state.json
fi

echo "[*] Copy influxdb"
if [ -e $tmpdir/etc/influxdb/influxdb.conf ]
then
    cp $tmpdir/etc/influxdb/influxdb.conf /etc/influxdb/influxdb.conf
fi
if [ -e $tmpdir/var/lib/influxdb ]
then
    cp -R $tmpdir/var/lib/influxdb /var/lib/
fi

echo "[*] Copy Grafana"
if [ -e $tmpdir/etc/grafana/grafana.ini ]
then
    cp -R $tmpdir/etc/grafana /etc/
fi
if [ -e $tmpdir/var/lib/grafana ]
then
    cp -R $tmpdir/var/lib/grafana /var/lib/
fi

sync
umount $tmpdir

echo "[*] Killing network"
killall -9 connmand

echo "--------------------------"
echo "Migration successfully done."
echo
echo "Rebooting now."
echo

reboot

