
#SRC_URI = "http://www.slimjetbrowser.com/chrome/lnx/chrome64_76.0.3809.100.deb"
SRC_URI = "https://www.slimjetbrowser.com/release/archive/30.0.3.0/slimjet_amd64.tar.xz"
SRC_URI[sha256sum] = "e1a4b34ce4b3cde46c74df3f64e226b0edef55b814617cec9d5972f3329578c3"

require widevine.inc

COMPATIBLE_HOST = "(x86_64).*-linux"
