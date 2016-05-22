package engineer.carrot.warren.warren.handler.rpl

import engineer.carrot.warren.kale.IKaleHandler
import engineer.carrot.warren.kale.irc.message.rpl.Rpl474Message
import engineer.carrot.warren.warren.loggerFor
import engineer.carrot.warren.warren.state.CaseMappingState
import engineer.carrot.warren.warren.state.ChannelsState
import engineer.carrot.warren.warren.state.JoiningChannelLifecycle

class Rpl474Handler(val channelsState: ChannelsState, val caseMappingState: CaseMappingState) : IKaleHandler<Rpl474Message> {
    private val LOGGER = loggerFor<Rpl474Handler>()

    override val messageType = Rpl474Message::class.java

    override fun handle(message: Rpl474Message) {
        val channel = channelsState.getJoining(message.channel, caseMappingState.mapping)

        if (channel == null) {
            LOGGER.warn("got a banned from channel reply for a channel we don't think we're joining: $message")
            LOGGER.trace("channels state: $channelsState")
            return
        }

        LOGGER.warn("we are banned from channel, failed to join: $channel")
        channel.status = JoiningChannelLifecycle.FAILED

        LOGGER.trace("new channels state: $channelsState")
    }
}

