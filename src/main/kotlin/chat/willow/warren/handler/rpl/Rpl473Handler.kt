package chat.willow.warren.handler.rpl

import chat.willow.kale.IKaleHandler
import chat.willow.kale.irc.message.rfc1459.rpl.Rpl473Message
import chat.willow.warren.helper.loggerFor
import chat.willow.warren.state.CaseMappingState
import chat.willow.warren.state.JoiningChannelLifecycle
import chat.willow.warren.state.JoiningChannelsState

class Rpl473Handler(val channelsState: JoiningChannelsState, val caseMappingState: CaseMappingState) : IKaleHandler<Rpl473Message> {

    private val LOGGER = loggerFor<Rpl473Handler>()

    override val messageType = Rpl473Message::class.java

    override fun handle(message: Rpl473Message, tags: Map<String, String?>) {
        val channel = channelsState[message.channel]

        if (channel == null) {
            LOGGER.warn("got an invite only channel reply for a channel we don't think we're joining: $message")
            LOGGER.trace("channels state: $channelsState")
            return
        }

        LOGGER.warn("channel is invite only, failed to join: $channel")
        channel.status = JoiningChannelLifecycle.FAILED

        LOGGER.trace("new channels state: $channelsState")
    }

}
