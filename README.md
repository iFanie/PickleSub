# PickleSub
Conditional Event Bus subscription
## Sample Usage
- Annotate with ```@SimplySubscribe``` to retreive all relevant events.
- Annotate with ```@ConditionallySubscribe``` and provide a function which must be satisfied by the event data in order to be retrieved.
- Build the project and use the generated subscriber class. For ```MainActivity``` the generated subscriber class would be ```MainActivitySubscriber```.
- Register and unregister using the ```PickleSub``` static helper functions.
```kotlin
class MainActivity : BaseActivity() {

    val bus = Bus(ThreadEnforcer.MAIN)
    val subscriber = MainActivitySubscriber(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bus.register(this)
        PickleSub.register(subscriber, bus)

        button.setOnClickListener {
            bus.post(Num.random(-5, 5))
        }
    }

    override fun onDestroy() {
        bus.unregister(this)
        PickleSub.unregister(subscriber, bus)

        super.onDestroy()
    }

    @SimplySubscribe
    fun onNumberReceived(number: Num) {
        Log.d(LOG, "onNumberReceived: ${number.value}")
    }

    @Subscribe
    fun onNumberReceivedOtto(number: Num) {
        Log.d(LOG, "onNumberReceivedOtto: ${number.value}")
    }

    @ConditionallySubscribe(mustSatisfy = "isPositive")
    fun onPositiveReceived(positiveNumber: Num) {
        Log.d(LOG, "onPositiveReceived: ${positiveNumber.value}")
    }

    companion object {
        const val LOG = "PickleSubApp:MAIN"
    }

}
```
### Works with inheritance
- The Subscriber will be generated only for the deriving class.
```kotlin
abstract class BaseActivity : AppCompatActivity() {

    @ConditionallySubscribe(mustSatisfy = "isPositive")
    fun onBasePositiveReceived(positiveNumber: Num) {
        Log.d(LOG, "onBasePositiveReceived: ${positiveNumber.value}")
    }

    @ConditionallySubscribe(mustSatisfy = "isNegative")
    fun onNegativeReceived(negativeNumber: Num) {
        Log.d(LOG, "onNegativeReceived: ${negativeNumber.value}")
    }

    fun isPositive(number: Num) = number.value > 0

    fun isNegative(number: Num) = number.value < 0

    companion object {
        const val LOG = "PickleSubApp:BASE"
    }

}
```