SUMMARY = "KNXD extends the IP-reach of the KNX bus and supports multiple concurrent clients"
SECTION = "base"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=8264535c0c4e9c6c335635c4026a8022"

SRC_URI = "https://github.com/knxd/knxd/archive/refs/tags/${PV}.tar.gz \
           file://knxd.service \
           file://knxd.socket \
           file://knxd.conf \
           "

SRC_URI[sha256sum] = "c8378bc6f671a5ab75edb51b23e839ee1adcdd00b372314ca9d2bdcd37fb70fb"

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
                         ${datadir}/eibclient.php \
                         ${datadir}/EIB* \
                        "

SYSTEMD_SERVICE_${PN} = "knxd.service knxd.socket"
#do not enable knxd daemon by default
SYSTEMD_AUTO_ENABLE_${PN} = "disable"
