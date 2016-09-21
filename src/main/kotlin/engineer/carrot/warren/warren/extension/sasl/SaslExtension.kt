package engineer.carrot.warren.warren.extension.sasl

import engineer.carrot.warren.kale.IKale
import engineer.carrot.warren.warren.IMessageSink
import engineer.carrot.warren.warren.extension.cap.CapState
import engineer.carrot.warren.warren.extension.cap.ICapExtension
import engineer.carrot.warren.warren.state.AuthCredentials
import engineer.carrot.warren.warren.state.AuthLifecycle
import engineer.carrot.warren.warren.state.IStateCapturing

data class SaslState(var shouldAuth: Boolean, var lifecycle: AuthLifecycle, var credentials: AuthCredentials?)

class SaslExtension(initialState: SaslState, private val kale: IKale, private val capState: CapState, private val sink: IMessageSink) : ICapExtension, IStateCapturing<SaslState> {

    internal var internalState: SaslState = initialState
    @Volatile override var state: SaslState = initialState.copy()

    val authenticateHandler: AuthenticateHandler by lazy { AuthenticateHandler(internalState, sink) }
    val rpl903Handler: Rpl903Handler by lazy { Rpl903Handler(capState, internalState, sink) }
    val rpl904Handler: Rpl904Handler by lazy { Rpl904Handler(capState, internalState, sink) }
    val rpl905Handler: Rpl905Handler by lazy { Rpl905Handler(capState, internalState, sink) }

    override fun captureStateSnapshot() {
        state = internalState.copy()
    }

    override fun setUp() {
        kale.register(authenticateHandler)
        kale.register(rpl903Handler)
        kale.register(rpl904Handler)
        kale.register(rpl905Handler)

        // FIXME: logic to delay registration should be in here somehow
        //  maybe event loop should know about registration phase and check each time state updates?
    }

    override fun tearDown() {
        kale.unregister(authenticateHandler)
        kale.unregister(rpl903Handler)
        kale.unregister(rpl904Handler)
        kale.unregister(rpl905Handler)
    }

}