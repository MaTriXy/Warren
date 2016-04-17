package engineer.carrot.warren.warren

import com.nhaarman.mockito_kotlin.*
import engineer.carrot.warren.kale.IKale
import engineer.carrot.warren.kale.IKaleHandler
import engineer.carrot.warren.kale.irc.message.IMessage
import engineer.carrot.warren.kale.irc.message.IrcMessage
import engineer.carrot.warren.kale.irc.message.rfc1459.NickMessage
import engineer.carrot.warren.kale.irc.message.rfc1459.UserMessage
import engineer.carrot.warren.warren.handler.PingHandler
import engineer.carrot.warren.warren.handler.Rpl005.Rpl005Handler
import engineer.carrot.warren.warren.handler.Rpl005.Rpl005PrefixHandler
import engineer.carrot.warren.warren.handler.Rpl376Handler
import engineer.carrot.warren.warren.state.*
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.MockitoAnnotations

class IrcRunnerTests {
    lateinit var runner: IIrcRunner
    lateinit var connectionState: ConnectionState

    lateinit var mockKale: MockKale
    lateinit var mockSink: IMessageSink
    lateinit var mockProcessor: IMessageProcessor

    @Before fun setUp() {
        connectionState = ConnectionState(server = "test.server", port = 6697, nickname = "test-nick", username = "test-nick")

        val userPrefixesState = UserPrefixesState(prefixesToModes = mapOf('@' to 'o', '+' to 'v'))
        val channelModesState = ChannelModesState(typeA = setOf('e', 'I', 'b'), typeB = setOf('k'), typeC = setOf('l'), typeD = setOf('i', 'm', 'n', 'p', 's', 't', 'S', 'r'))
        val channelPrefixesState = ChannelTypesState(types = setOf('#', '&'))
        val parsingState = ParsingState(userPrefixesState, channelModesState, channelPrefixesState)

        val initialState = IrcState(connectionState, parsingState)

        mockKale = MockKale

        mockSink = mock()
        mockProcessor = mock()

        runner = IrcRunner(mockKale, mockSink, mockProcessor, initialState)

        MockitoAnnotations.initMocks(this)
    }

    @Test fun test_run_RegistersHandlers() {
        runner.run()

        assertEquals(3, mockKale.spyRegisterHandlers.size)
        assertTrue(mockKale.spyRegisterHandlers[0] is PingHandler)
        assertTrue(mockKale.spyRegisterHandlers[1] is Rpl005Handler)
        assertTrue(mockKale.spyRegisterHandlers[2] is Rpl376Handler)
    }

    @Test fun test_run_SendsRegistrationMessages() {
        runner.run()

        val inOrder = inOrder(mockSink)
        inOrder.verify(mockSink).write(NickMessage(nickname = connectionState.nickname))
        inOrder.verify(mockSink).write(UserMessage(username = connectionState.nickname, mode = "8", realname = connectionState.nickname))
    }

    @Test fun test_run_ProcessesOnce() {
        runner.run()

        verify(mockProcessor).process()
    }

}

object MockKale: IKale {
    var spyRegisterHandlers = mutableListOf<IKaleHandler<*>>()

    override fun <T : IMessage> register(handler: IKaleHandler<T>) {
        spyRegisterHandlers.add(handler)
    }

    override fun <T : IMessage> serialise(message: T): IrcMessage? {
        throw UnsupportedOperationException()
    }

    override fun process(line: String) {
        throw UnsupportedOperationException()
    }

}