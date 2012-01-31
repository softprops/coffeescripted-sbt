#!/bin/sh

echo "fetching latest iced coffeescript compiler"

if [[ -f src/main/resources/iced/coffee-script-iced.js && ! -f src/main/resources/iced/coffee-script.js.released ]]; then
  echo "stashing old iced coffee"
  mv src/main/resources/iced/coffee-script-iced.js src/main/resources/iced/coffee-script-iced.js.released
fi

curl -o src/main/resources/iced/coffee-script.js --progress-bar -O http://maxtaco.github.com/coffee-script/extras/coffee-script.js