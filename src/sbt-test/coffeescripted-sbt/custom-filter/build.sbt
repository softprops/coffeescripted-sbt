seq(coffeeSettings:_*)

(CoffeeKeys.filter in (Compile, CoffeeKeys.coffee)) := ("*.coffee" - "*.no.coffee")
