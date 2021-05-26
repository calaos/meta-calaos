DESCRIPTION = "Image with Calaos Server only."

LICENSE = "MIT"

include calaos-os-minimal.bb

MAGE_LINGUAS = "en-us fr-fr de-de es-es hi-in"

IMAGE_INSTALL += "haproxy"
IMAGE_INSTALL += "calaos-server calaos-web"
IMAGE_INSTALL += "calaos-ddns"
IMAGE_INSTALL += "influxdb grafana"

IMAGE_INSTALL += "lua-socket"
IMAGE_INSTALL += "linuxconsoletools"

