
SRC_URI = "https://dl.google.com/widevine-cdm/4.10.2209.1-linux-ia32.zip"
SRC_URI[sha256sum] = "7e51ee15ce93cfe906acde19abcdc1b8fb0a5dbfa64c06b593a3c21027526c65"

require widevine.inc

COMPATIBLE_HOST = "(i686).*-linux"
