DESCRIPTION = "Widevine DRM to make qtwebengine works reliably"

LICENSE = "CLOSED"

INSANE_SKIP_${PN} = "ldflags"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT  = "1"

do_install () {
	install -m 0755 -d ${D}${libdir}/plugins/ppapi
	install -m 0755 ${WORKDIR}/libwidevinecdm.so ${D}${libdir}/plugins/ppapi
}

FILES_${PN} = "${libdir}/plugins/ppapi"

#Only default package is needed, no -dev/-doc/-locale
PACKAGES = "${PN}"

