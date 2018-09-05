# PickleSub
Conditional Event Bus subscription
## Install
```gradle
implementation 'com.izikode.izilib:picklesub:0.3'
annotationProcessor 'com.izikode.izilib:picklesub-complier:0.3'
```
##### or with kotlin
```gradle
implementation 'com.izikode.izilib:picklesub:0.3'
kapt 'com.izikode.izilib:picklesub-complier:0.3'
```
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
## Licence
```licence
Copyright 2018 Fanie Veizis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```