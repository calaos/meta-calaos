SUMMARY = "KNXD extends the IP-reach of the KNX bus and supports multiple concurrent clients"
SECTION = "base"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=8264535c0c4e9c6c335635c4026a8022"

SRC_URI = "git://github.com/knxd/knxd.git;protocol=https;branch=master \
           file://knxd.service \
           file://knxd.socket \
           file://knxd.conf \
           "

SRCREV = "7193c4ab247b17e9805c247cccb7edf61f72eb39"
S = "${WORKDIR}/git"

inherit autotools-brokensep gettext pkgconfig systemd

EXTRA_OECONF = "--enable-eibnetip --enable-eibnetiptunnel --enable-usb --enable-eibnetipserver --enable-systemd \
                --enable-ft12 --enable-dummy --enable-groupcache --enable-tpuart \
               "

DEPENDS += "libev systemd libusb fmt"
RDEPENDS_${PN} = "libev"

do_install_append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/knxd.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/knxd.socket ${D}${systemd_unitdir}/system
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/knxd.conf ${D}${sysconfdir}

    rm -r ${D}${libdir}/sysusers.d
}

PACKAGES =+ " ${PN}-examples-dbg  ${PN}-examples"

FILES_${PN}-examples += "${datadir}/knxd/examples \
                         ${datadir}/knxd/eibclient.php \
                         ${datadir}/knxd/EIB* \
                        "

SYSTEMD_SERVICE_${PN} = "knxd.service knxd.socket"
#do not enable knxd daemon by default
SYSTEMD_AUTO_ENABLE_${PN} = "disable"
