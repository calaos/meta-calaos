DESCRIPTION = "Calaos dynamic dns client tool"

inherit go systemd

SRC_URI = " \
    git://${GO_IMPORT};branch=main \
    file://calaos-dns.timer \
    file://calaos-dns.service \
    "

SRCREV = "${AUTOREV}"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/COPYING;md5=d32239bcb673463ab874e80d47fae504"

GO_IMPORT = "github.com/calaos/calaos_ddns"
export GO111MODULE = "off"

do_install_append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/calaos-dns.timer ${D}${systemd_system_unitdir}/calaos-dns.timer
    install -m 0644 ${WORKDIR}/calaos-dns.service ${D}${systemd_system_unitdir}/calaos-dns.service

    install -d ${D}${sysconfdir}/haproxy
    install -m 0644 ${S}/src/${GO_IMPORT}/haproxy/haproxy.template ${D}${sysconfdir}/haproxy/haproxy.template
}

SYSTEMD_SERVICE_${PN} = "\
    calaos-dns.service \
    calaos-dns.timer \
"

