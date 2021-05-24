DESCRIPTION = "Scalable datastore for metrics, events, and real-time analytics"

SRC_URI = " \
    git://github.com/influxdata/influxdb.git;protocol=http;branch=1.7;destsuffix=${PN}-${PV}/src/github.com/influxdata/influxdb \
    "

SRCREV = "23bc63d43a8dc05f53afa46e3526ebb5578f3d88"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/github.com/influxdata/influxdb/LICENSE;md5=ba8146ad9cc2a128209983265136e06a"

DEPENDS += "go-native"

do_configure_append() {
    cd ${WORKDIR}/${PN}-${PV}/src/github.com/influxdata/influxdb
    GOPATH=${WORKDIR}/${PN}-${PV} dep ensure -vendor-only
}

