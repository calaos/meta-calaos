# Inspired by Angstrom recipe angstrom-version.Bb

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

PV = "${DISTRO_VERSION}"

PACKAGES = "${PN}"
PACKAGE_ARCH = "${MACHINE_ARCH}"

def get_layers(bb, d):
    layers = (d.getVar("BBLAYERS", 1) or "").split()
    layers_branch_rev = ["%-17s = \"%s:%s\"" % (os.path.basename(i), \
        base_get_metadata_git_branch(i, None).strip().strip('()'), \
        base_get_metadata_git_revision(i, None)) \
        for i in layers]
    i = len(layers_branch_rev)-1
    p1 = layers_branch_rev[i].find("=")
    s1= layers_branch_rev[i][p1:]
    while i > 0:
        p2 = layers_branch_rev[i-1].find("=")
        s2= layers_branch_rev[i-1][p2:]
        if s1 == s2:
            layers_branch_rev[i-1] = layers_branch_rev[i-1][0:p2]
            i -= 1
        else:
            i -= 1
            p1 = layers_branch_rev[i].find("=")
            s1= layers_branch_rev[i][p1:]

    layertext = "Configured Openembedded layers:\n%s\n" % '\n'.join(layers_branch_rev)
    layertext = layertext.replace('<','')
    layertext = layertext.replace('>','')
    return layertext

do_install() {
             install -d ${D}${sysconfdir}
             echo "${DISTRO_NAME}" > ${D}${sysconfdir}/calaos-version
             echo "${DISTRO_VERSION}" > ${D}${sysconfdir}/calaos-version
             echo "${MACHINE}" >> ${D}${sysconfdir}/calaos-version

             echo "${@get_layers(bb, d)}" > ${D}${sysconfdir}/calaos-build-info

             echo "VERSION=\"${DISTRO_VERSION}\"" > ${D}${sysconfdir}/os-release
             echo "NAME=\"Calaos\"" >> ${D}${sysconfdir}/os-release
             echo "ID=\"calaos\"" >> ${D}${sysconfdir}/os-release
             echo "PRETTY_NAME=\"Calaos OS ${DISTRO_VERSION}\"" >> ${D}${sysconfdir}/os-release
             echo "ANSI_COLOR=\"1;35\"" >> ${D}${sysconfdir}/os-release
             echo "HOME_URL=\"http://www.calaos.fr\"" >> ${D}${sysconfdir}/os-release
}

RPROVIDES_${PN} = "os-release"
RREPLACES_${PN} = "os-release"
RCONFLICTS_${PN} = "os-release"
