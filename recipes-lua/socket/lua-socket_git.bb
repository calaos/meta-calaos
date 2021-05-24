DESCRIPTION = "LuaSocket is the most comprehensive networking support library for the Lua language."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ab6706baf6d39a6b0fa2613a3b0831e7"
HOMEPAGE = "https://github.com/diegonehab/luasocket"

PR = "r0"
S = "${WORKDIR}/git"

DEPENDS = "luajit"

SRC_URI = "git://github.com/diegonehab/luasocket.git"
#           file://0001-allow-overrides-for-DESTDIR-CC_linux-LD_linux-LDFLAG.patch"
SRCREV = "5b18e475f38fcf28429b1cc4b17baee3b9793a62"

TARGET_CC_ARCH += "${LDFLAGS}"

CFLAGS += "-I${STAGING_INCDIR}/luajit-2.1"
EXTRA_OEMAKE = "PLAT=linux LUAINC_linux=${STAGING_INCDIR}/luajit-2.1 LUAPREFIX_linux=${prefix} DESTDIR=${D} LUAV=5.1 CC_linux='${CC}' LD_linux='${CC}'"

do_install() {
        oe_runmake install
}

FILES_${PN}  += "${libdir}/* ${datadir}/lua/5.*/"
FILES_${PN}-dbg  += "${libdir}/lua/5.*/.debug ${libdir}/lua/5.*/*/.debug"

