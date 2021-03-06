package chat.willow.warren.handler

import chat.willow.kale.IMetadataStore
import chat.willow.kale.KaleHandler
import chat.willow.kale.helper.equalsIgnoreCase
import chat.willow.kale.irc.message.rfc1459.NickMessage
import chat.willow.warren.helper.loggerFor
import chat.willow.warren.state.CaseMappingState
import chat.willow.warren.state.ConnectionState
import chat.willow.warren.state.JoinedChannelsState

class NickHandler(val connectionState: ConnectionState, val channelsState: JoinedChannelsState, val caseMappingState: CaseMappingState) : KaleHandler<NickMessage.Message>(NickMessage.Message.Parser) {

    private val LOGGER = loggerFor<NickHandler>()


    override fun handle(message: NickMessage.Message, metadata: IMetadataStore) {
        val from = message.source
        val to = message.nickname

        if (equalsIgnoreCase(caseMappingState.mapping, from.nick, connectionState.nickname)) {
            // We were forcibly renamed by the server

            connectionState.nickname = from.nick
        }

        for ((name, channel) in channelsState.all) {
            val user = channel.users[from.nick]
            if (user != null) {
                channel.users -= from.nick
                channel.users += user.copy(prefix = from.copy(nick = to))
            }
        }

        LOGGER.trace("someone changed nick - new states: $connectionState, $channelsState")
    }

}