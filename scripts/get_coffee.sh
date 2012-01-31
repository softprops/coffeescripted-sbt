#!/bin/sh

echo "fetching latest vanilla coffeescript compiler"

if [[ -f src/main/resources/vanilla/coffee-script.js && ! -f src/main/resources/vanilla/coffee-script.js.released ]]; then
  echo "stashing old coffee"
  mv src/main/resources/vanilla/coffee-script.js src/main/resources/vanilla/coffee-script.js.released
fi

curl -o src/main/resources/vanilla/coffee-script.js --progress-bar -O coffeescript.org/extras/coffee-script.js