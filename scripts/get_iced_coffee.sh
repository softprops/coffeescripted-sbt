#!/bin/sh

echo "fetching latest iced coffeescript compiler"
CS=http://maxtaco.github.com/coffee-script/extras/coffee-script.js

if [[ -f src/main/resources/iced/coffee-script.js && ! -f src/main/resources/iced/coffee-script.js.released ]]; then
  echo "stashing old iced coffee"
  mv src/main/resources/iced/coffee-script.js src/main/resources/iced/coffee-script.js.released
fi

curl -o src/main/resources/iced/coffee-script.js --progress-bar -O $CS