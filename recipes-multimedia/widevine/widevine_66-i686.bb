
#SRC_URI = "http://www.slimjetbrowser.com/chrome/lnx/chrome32_48.0.2564.109.deb"
SRC_URI = "https://www.slimjetbrowser.com/release/archive/30.0.3.0/slimjet_i386.tar.xz"
SRC_URI[sha256sum] = "7e51ee15ce93cfe906acde19abcdc1b8fb0a5dbfa64c06b593a3c21027526c65"

require widevine.inc

COMPATIBLE_HOST = "(i686).*-linux"
