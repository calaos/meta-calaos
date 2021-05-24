DESCRIPTION = "Image with Calaos."
DESCRIPTION = "Calaos image for developper contains debugging tools, commpiler, and dev packages"

IMAGE_FEATURES += "package-management x11-base dbg-pkgs tools-debug tools-testapps debug-tweaks dev-pkgs"

LICENSE = "MIT"

include calaos-os-minimal.bb

IMAGE_LINGUAS = "en-us fr-fr de-de es-es hi-in"

IMAGE_INSTALL += "connman"
IMAGE_INSTALL += "haproxy"
IMAGE_INSTALL += "calaos-server calaos-home calaos-web calaos-base"
IMAGE_INSTALL += "tzdata tzdata-europe ntp"
IMAGE_INSTALL += "avahi-utils"
IMAGE_INSTALL += "nano htop procps"
IMAGE_INSTALL += "linuxconsoletools"
IMAGE_INSTALL += "openssl ca-certificates"
IMAGE_INSTALL += "cpu-performance"
IMAGE_INSTALL += "connman-wait-online connman-client connman-autoconnect"
IMAGE_INSTALL += "setxkbmap"
IMAGE_INSTALL += "wget"

IMAGE_INSTALL += "edje-utils elementary-tests"

IMAGE_INSTALL += "autoconf automake binutils binutils-symlinks cpp cpp-symlinks gcc gcc-symlinks g++ g++-symlinks gettext make libstdc++ libstdc++-dev file coreutils"

XSERVER_append_intel-corei7-64 = "xf86-video-vesa"
XSERVER_append_intel-core2-32 = "xf86-video-vesa"
