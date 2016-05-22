package engineer.carrot.warren.warren.state

import engineer.carrot.warren.kale.irc.message.utility.CaseMapping

data class IrcState(val connection: ConnectionState, val parsing: ParsingState, val channels: ChannelsState)

data class ChannelsState(val joining: MutableMap<String, JoiningChannelState> = mutableMapOf(), val joined: MutableMap<String, ChannelState>) {

    // Joining channels

    fun containsJoining(channel: String, mapping: CaseMapping): Boolean {
        return joining.containsKey(mapping.toLower(channel))
    }

    fun getJoining(channel: String, mapping: CaseMapping): JoiningChannelState? {
        return joining[mapping.toLower(channel)]
    }

    fun putJoining(channel: JoiningChannelState, mapping: CaseMapping) {
        joining[mapping.toLower(channel.name)] = channel
    }

    fun removeJoining(channel: String, mapping: CaseMapping): JoiningChannelState? {
        return joining.remove(mapping.toLower(channel))
    }
    
    // Joined channels
    
    fun containsJoined(channel: String, mapping: CaseMapping): Boolean {
        return joined.containsKey(mapping.toLower(channel))
    }

    fun getJoined(channel: String, mapping: CaseMapping): ChannelState? {
        return joined[mapping.toLower(channel)]
    }

    fun putJoined(channel: ChannelState, mapping: CaseMapping) {
        joined[mapping.toLower(channel.name)] = channel
    }

    fun removeJoined(channel: String, mapping: CaseMapping): ChannelState? {
        return joined.remove(mapping.toLower(channel))
    }

}

data class JoiningChannelState(val name: String, val key: String? = null, var status: JoiningChannelLifecycle) {
    override fun toString(): String {
        return "JoiningChannelState(name=$name, key=${if (key == null) {
            "null"
        } else {
            "***"
        }}, status=$status)"
    }
}

enum class JoiningChannelLifecycle { JOINING, FAILED }

data class ChannelState(val name: String, val users: MutableMap<String, ChannelUserState>, var topic: String? = null)

data class ChannelUserState(val nick: String, val modes: MutableSet<Char> = mutableSetOf())

data class ConnectionState(val server: String, val port: Int, var nickname: String, val username: String, var lifecycle: LifecycleState, val cap: CapState, val sasl: SaslState)

enum class LifecycleState { CONNECTING, REGISTERING, CONNECTED, DISCONNECTED }

data class CapState(var lifecycle: CapLifecycle, var negotiate: Set<String>, var server: Map<String, String?>, var accepted: Set<String>, var rejected: Set<String>)

enum class CapLifecycle { NEGOTIATING, NEGOTIATED, FAILED }

data class SaslState(var shouldAuth: Boolean, var lifecycle: SaslLifecycle, var credentials: SaslCredentials?)

enum class SaslLifecycle { NO_AUTH, AUTHING, AUTHED, AUTH_FAILED }

data class SaslCredentials(val account: String, val password: String) {
    override fun toString(): String {
        return "SaslCredentials(account=$account, password=***)"
    }
}

data class ParsingState(val userPrefixes: UserPrefixesState, val channelModes: ChannelModesState, val channelTypes: ChannelTypesState, val caseMapping: CaseMappingState)

data class UserPrefixesState(var prefixesToModes: Map<Char, Char>)

data class ChannelModesState(var typeA: Set<Char>, var typeB: Set<Char>, var typeC: Set<Char>, var typeD: Set<Char>)

data class ChannelTypesState(var types: Set<Char>)

data class CaseMappingState(var mapping: CaseMapping)