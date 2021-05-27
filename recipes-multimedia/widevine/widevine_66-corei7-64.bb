
SRC_URI = "https://dl.google.com/widevine-cdm/4.10.2209.1-linux-x64.zip"
SRC_URI[sha256sum] = "8c7dc18cb6454a3479283ef7962f9108381f234e37297bf7eb837af9d5eccbd6"

require widevine.inc

COMPATIBLE_HOST = "(x86_64).*-linux"
