DESCRIPTION = "Connman Autoconnect all interfaces"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-or-later;md5=fed54355545ffd980b814dab4a3b312c"

SRC_URI = "file://connman-autoconnect \
           file://connman-autoconnect.service"

PR = "r0"

inherit systemd

do_install() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/connman-autoconnect.service ${D}${systemd_unitdir}/system
    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/connman-autoconnect ${D}${sbindir}/
}

SYSTEMD_SERVICE_${PN} = "connman-autoconnect.service"
