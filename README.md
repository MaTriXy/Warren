# Warren

Kotlin, unit tested, IRC v3.2 state management and observing. Made for personal use, and open sourced. Intended to provide the tools needed to make IRC related software, like bridges and bots.

[Kale](https://github.com/CarrotCodes/Kale) is the parsing and serialising counterpart.

[Thump](https://github.com/CarrotCodes/Thump) is the primary upstream project - a bridge that lets people chat between Minecraft and IRC whilst they play.

Though still in active development, it remains stable enough for Thump to use. Thump, Warren and Kale drive requirements between themselves.

## Why is this better than other IRC frameworks?

Warren and Kale have a few advantages over other IRC frameworks:

* The responsibilities of parsing and state management are separated
* Both parsing and state management are verified by hundreds of unit tests
* Messages, and state handlers, are individually encapsulated
 * Dependencies are clear, and there are no enormous, unverifiable disaster zones

## Example usage

The project includes a simple [example runner](https://github.com/CarrotCodes/Warren/blob/develop/src/main/kotlin/engineer/carrot/warren/warren/WarrenRunner.kt) that prints out events as they happen, logs in using SASL and replies to me saying `rabbit party` in a channel.

If you're interested in more complex usage, come talk to me on IRC: #carrot on [ImaginaryNet](http://imaginarynet.uk/)

```kotlin
val eventDispatcher = WarrenEventDispatcher()
eventDispatcher.onAnything {
    LOGGER.info("event: $it")
}

val connection = createRunner(server, port, (port != 6667), nickname, password, mapOf("#botdev" to null), eventDispatcher, fireIncomingLineEvent = true)

eventDispatcher.on(ChannelMessageEvent::class) {
    LOGGER.info("channel message: $it")

    if (it.user.nick == "carrot" && it.message.equals("rabbit party", ignoreCase = true)) {
        connection.eventSink.add(SendSomethingEvent(PrivMsgMessage(target = it.channel, message = "🐰🎉"), connection.sink))
    }
}

connection.run()
```

## TODO

* [RFC 1459](https://tools.ietf.org/html/rfc1459)
 * Essentials are done - last remaining thing is MODE tracking
* [IRC v3](http://ircv3.net/irc/)
 * 3.1 done - consider how to integrate tags like `account-tag` in to message parsing
 * Goal is full 3.1 and 3.2 compliance by default
* NickServ identification (SASL only at the moment)
* Modes are parsed and tracked but events are not fired for mode changes yet

## Code License
The source code of this project is licensed under the terms of the ISC license, listed in the [LICENSE](LICENSE.md) file. A concise summary of the ISC license is available at [choosealicense.org](http://choosealicense.com/licenses/isc/).

## Building
This project uses Gradle and IntelliJ IDEA for pretty easy setup and building.

Basic usage:
* **Setup**: `./gradlew clean idea`
* **Building**: `./gradlew clean build` - this will also produce a fat Jar with shaded dependencies included
