DESCRIPTION = "The tool for beautiful monitoring and metric analytics & dashboards for Graphite, InfluxDB & Prometheus & More"

SRC_URI = " \
    git://github.com/grafana/grafana.git;protocol=http;branch=v6.4.x;destsuffix=${PN}-${PV}/src/github.com/grafana/grafana \
"

SRCREV = "62b53c3eb7e4b93a4a23e2fdca2b28b28503fac5"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/github.com/grafana/grafana/LICENSE;md5=31f6db4579f7bbf48d02bff8c5c3a6eb"

DEPENDS += "go-native"

inherit npm go systemd

SRC_URI += "\
	file://grafana.service \
	file://grafana-server \
"

GO_IMPORT = "github.com/grafana/grafana"
GO_INSTALL = "\
	${GO_IMPORT}/pkg/cmd/grafana-server \
	${GO_IMPORT}/pkg/cmd/grafana-cli \
"

#Fixes libpthread.so.0: error adding symbols: DSO missing from command line
LDFLAGS += " -lpthread"

do_configure_append() {
    oe_runnpm_native install -g yarn
    cd ${WORKDIR}/${PN}-${PV}/src/github.com/grafana/grafana
    yarn install --pure-lockfile --no-progress
    yarn run build
}

do_install_append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/grafana.service ${D}${systemd_system_unitdir}/grafana.service
    
    install -d ${D}${sysconfdir}/default
    install -m 0644 ${WORKDIR}/grafana-server ${D}${sysconfdir}/default/
    
    install -d ${D}${sysconfdir}/grafana
    install -m 0644 ${WORKDIR}/${PN}-${PV}/src/github.com/grafana/grafana/conf/defaults.ini ${D}${sysconfdir}/grafana/grafana.ini

    for d in dashboards datasources notifiers
    do
        install -d ${D}${sysconfdir}/grafana/provisioning/${d}
        install -m 0644 ${WORKDIR}/${PN}-${PV}/src/github.com/grafana/grafana/conf/provisioning/${d}/sample.yaml ${D}${sysconfdir}/grafana/provisioning/${d}/sample.yaml
    done

    # install frontend
    install -d ${D}${datadir}/grafana
    cp -R --no-dereference --preserve=mode,links -v ${WORKDIR}/${PN}-${PV}/src/github.com/grafana/grafana/public ${D}${datadir}/grafana/

    #remove unneeded files and prevent packaging QA errors
    rm -fr ${WORKDIR}/${PN}-${PV}/src/github.com/grafana/grafana/scripts

    #remove source files, this take too much space and time to package and is not needed anyway
    rm -fr ${D}/${libdir}/go/src
}

SYSTEMD_SERVICE_${PN} = "\
    grafana.service \
"

SYSTEMD_AUTO_ENABLE_${PN} = "disable"

FILES_${PN} += "\
    ${systemd_unitdir} \
    ${sysconfdir}/grafana \
    ${sysconfdir}/default \
"

