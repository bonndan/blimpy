package com.github.bonndan.blimpy.blimp.entity.engine

import com.mojang.serialization.Codec
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistryAccess
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional
import java.util.stream.Stream

class EngineTest {

    private class MemoryValueNode {
        val values = mutableMapOf<String, Any?>()
        val children = mutableMapOf<String, MemoryValueNode>()
    }

    private class MemoryValueOutput(private val node: MemoryValueNode) : ValueOutput {
        override fun <T : Any> store(name: String, codec: Codec<T>, value: T) {
            node.values[name] = value
        }

        override fun <T : Any> storeNullable(name: String, codec: Codec<T>, value: T?) {
            node.values[name] = value
        }

        @Deprecated("Deprecated in interface")
        override fun <T : Any> store(codec: com.mojang.serialization.MapCodec<T>, value: T) {
            throw UnsupportedOperationException()
        }

        override fun putBoolean(name: String, value: Boolean) {
            node.values[name] = value
        }

        override fun putByte(name: String, value: Byte) {
            node.values[name] = value
        }

        override fun putShort(name: String, value: Short) {
            node.values[name] = value
        }

        override fun putInt(name: String, value: Int) {
            node.values[name] = value
        }

        override fun putLong(name: String, value: Long) {
            node.values[name] = value
        }

        override fun putFloat(name: String, value: Float) {
            node.values[name] = value
        }

        override fun putDouble(name: String, value: Double) {
            node.values[name] = value
        }

        override fun putString(name: String, value: String) {
            node.values[name] = value
        }

        override fun putIntArray(name: String, value: IntArray) {
            node.values[name] = value
        }

        override fun child(name: String): ValueOutput {
            return MemoryValueOutput(node.children.computeIfAbsent(name) { MemoryValueNode() })
        }

        override fun childrenList(name: String): ValueOutput.ValueOutputList {
            throw UnsupportedOperationException()
        }

        override fun <T : Any> list(name: String, codec: Codec<T>): ValueOutput.TypedOutputList<T> {
            throw UnsupportedOperationException()
        }

        override fun discard(name: String) {
            node.values.remove(name)
            node.children.remove(name)
        }

        override fun isEmpty(): Boolean = node.values.isEmpty() && node.children.isEmpty()
    }

    private class MemoryValueInput(private val node: MemoryValueNode?) : ValueInput {
        override fun <T : Any> read(name: String, codec: Codec<T>): Optional<T> {
            @Suppress("UNCHECKED_CAST")
            val value = node?.values?.get(name) as T?
            return Optional.ofNullable(value)
        }

        @Deprecated("Deprecated in interface")
        override fun <T : Any> read(codec: com.mojang.serialization.MapCodec<T>): Optional<T> {
            throw UnsupportedOperationException()
        }

        override fun child(name: String): Optional<ValueInput> {
            return Optional.ofNullable(node?.children?.get(name)?.let(::MemoryValueInput))
        }

        override fun childOrEmpty(name: String): ValueInput {
            return MemoryValueInput(node?.children?.get(name))
        }

        override fun childrenList(name: String): Optional<ValueInput.ValueInputList> {
            throw UnsupportedOperationException()
        }

        override fun childrenListOrEmpty(name: String): ValueInput.ValueInputList {
            return object : ValueInput.ValueInputList {
                override fun isEmpty(): Boolean = true
                override fun iterator(): MutableIterator<ValueInput> = mutableListOf<ValueInput>().iterator()
                override fun stream(): Stream<ValueInput> = Stream.empty()
            }
        }

        override fun <T : Any> list(name: String, codec: Codec<T>): Optional<ValueInput.TypedInputList<T>> {
            throw UnsupportedOperationException()
        }

        override fun <T : Any> listOrEmpty(name: String, codec: Codec<T>): ValueInput.TypedInputList<T> {
            return object : ValueInput.TypedInputList<T> {
                override fun isEmpty(): Boolean = true
                override fun iterator(): MutableIterator<T> = mutableListOf<T>().iterator()
                override fun stream(): Stream<T> = Stream.empty()
            }
        }

        override fun getBooleanOr(name: String, defaultValue: Boolean): Boolean {
            return node?.values?.get(name) as? Boolean ?: defaultValue
        }

        override fun getByteOr(name: String, defaultValue: Byte): Byte {
            return node?.values?.get(name) as? Byte ?: defaultValue
        }

        override fun getShortOr(name: String, defaultValue: Short): Int {
            return node?.values?.get(name) as? Int ?: defaultValue.toInt()
        }

        override fun getInt(name: String): Optional<Int> {
            return Optional.ofNullable(node?.values?.get(name) as? Int)
        }

        override fun getIntOr(name: String, defaultValue: Int): Int {
            return node?.values?.get(name) as? Int ?: defaultValue
        }

        override fun getLongOr(name: String, defaultValue: Long): Long {
            return node?.values?.get(name) as? Long ?: defaultValue
        }

        override fun getLong(name: String): Optional<Long> {
            return Optional.ofNullable(node?.values?.get(name) as? Long)
        }

        override fun getFloatOr(name: String, defaultValue: Float): Float {
            return node?.values?.get(name) as? Float ?: defaultValue
        }

        override fun getDoubleOr(name: String, defaultValue: Double): Double {
            return node?.values?.get(name) as? Double ?: defaultValue
        }

        override fun getString(name: String): Optional<String> {
            return Optional.ofNullable(node?.values?.get(name) as? String)
        }

        override fun getStringOr(name: String, defaultValue: String): String {
            return node?.values?.get(name) as? String ?: defaultValue
        }

        override fun getIntArray(name: String): Optional<IntArray> {
            return Optional.ofNullable(node?.values?.get(name) as? IntArray)
        }

        @Deprecated("Deprecated in interface")
        override fun lookup(): HolderLookup.Provider {
            throw UnsupportedOperationException()
        }
    }

    private open class TestEngine(saveStateCallback: SaveStateCallback) : Engine(saveStateCallback) {
        override fun isItemValid(slot: Int, stack: ItemStack): Boolean = false

        override fun calculateBurnTimeOfNextItem(stack: ItemStack): Int = 0

        override fun getEmissions(): Emissions = SmokeGenerator
    }

    private class PersistedValueEngine(saveStateCallback: SaveStateCallback) : TestEngine(saveStateCallback) {
        var persistedFuelCount = 0

        override fun serialize(output: ValueOutput) {
            output.putInt("persistedFuelCount", persistedFuelCount)
        }

        override fun deserialize(input: ValueInput) {
            persistedFuelCount = input.getIntOr("persistedFuelCount", 0)
        }
    }

    var engineStateUpdate = false
    var remainingBurnTimeUpdate = 0

    private val saveStateCallback: SaveStateCallback = object : SaveStateCallback {

        override fun saveState(engineState: Boolean, remainingBurnTime: Int) {
            engineStateUpdate = engineState
            remainingBurnTimeUpdate = remainingBurnTime
        }
    }

    lateinit var engine: Engine

    @BeforeEach
    fun init() {
        engine = TestEngine(saveStateCallback = saveStateCallback)
    }

    @Test
    fun `when engine is on and has remaining burn time then it is lit`() {

        engine.setRemainingBurnTime(100)
        engine.setEngineOn(true)

        assertThat(engine.isLit()).isTrue
    }

    @Test
    fun `when engine is off and has remaining burn time then it is not lit`() {

        engine.setRemainingBurnTime(100)
        engine.setEngineOn(false)

        assertThat(engine.isLit()).isFalse
    }

    @Test
    fun `when engine is on and has no remaining burn time then it is not lit`() {

        engine.setRemainingBurnTime(0)
        engine.setEngineOn(true)

        assertThat(engine.isLit()).isFalse
    }

    @Test
    fun `fuel in engine slot survives save and load`() {
        val saved = MemoryValueNode()
        val savingEngine = PersistedValueEngine(saveStateCallback)

        savingEngine.persistedFuelCount = 7
        savingEngine.addAdditionalSaveData(MemoryValueOutput(saved))

        val loadedEngine = PersistedValueEngine(saveStateCallback)
        loadedEngine.readAdditionalSaveData(MemoryValueInput(saved), RegistryAccess.EMPTY)

        assertThat(loadedEngine.persistedFuelCount).isEqualTo(7)
    }

}