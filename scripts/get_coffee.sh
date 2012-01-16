#!/bin/sh

echo "fetching latest coffeescript compiler"

if [[ -f src/main/resources/coffee-script.js && ! -f src/main/resources/coffee-script.js.released ]]; then
  echo "stashing old coffee"
  mv src/main/resources/coffee-script.js src/main/resources/coffee-script.js.released
fi

curl -o src/main/resources/coffee-script.js --progress-bar -O coffeescript.org/extras/coffee-script.js