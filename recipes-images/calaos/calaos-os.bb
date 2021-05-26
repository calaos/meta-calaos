DESCRIPTION = "Image with Calaos Server and UI."

LICENSE = "MIT"

include calaos-os-minimal.bb
IMAGE_FEATURES_append_intel-corei7-64 = " x11-base"
IMAGE_FEATURES_append_intel-core2-32 = " x11-base"

IMAGE_LINGUAS = "en-us fr-fr de-de es-es hi-in"

IMAGE_INSTALL += "haproxy"
IMAGE_INSTALL += "calaos-server calaos-web calaos-server-dbg"
IMAGE_INSTALL += "calaos-mobile calaos-mobile-tools"
IMAGE_INSTALL += "calaos-ddns"
IMAGE_INSTALL += "influxdb grafana"

IMAGE_INSTALL += "lua-socket"
IMAGE_INSTALL += "linuxconsoletools"

IMAGE_INSTALL += "evtest xev"
IMAGE_INSTALL += "xinput-calibrator"
IMAGE_INSTALL += "xf86-input-tslib tslib"

IMAGE_INSTALL += "screen lsof htop"
#IMAGE_INSTALL += "gdb calaos-server-dbg"

IMAGE_INSTALL_append_intel-corei7-64 = " mesa-megadriver setxkbmap widevine"
IMAGE_INSTALL_append_intel-core2-32 = " mesa-megadriver setxkbmap widevine"

XSERVER_append_intel-corei7-64 = " xf86-video-vesa"
XSERVER_append_intel-core2-32 = " xf86-video-vesa"

