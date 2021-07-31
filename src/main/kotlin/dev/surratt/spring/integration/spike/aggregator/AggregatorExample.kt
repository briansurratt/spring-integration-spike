package dev.surratt.spring.integration.spike.aggregator

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.aggregator.CorrelationStrategy
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.integration.dsl.AggregatorSpec
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.IntegrationFlows
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import java.util.*

data class FileMessage(val batchId: UUID) {
    val id: UUID = UUID.randomUUID()
}

data class TombstoneMessage(val batchId: UUID) {
    val fileIds = mutableListOf<UUID>()
}

@Configuration
class AggregatorExampleConfiguration {

    @Bean
    @Qualifier("aggregatorFileChannel")
    fun fileChannel(): MessageChannel {
        val channel = PublishSubscribeChannel()
        channel.setDatatypes(
            FileMessage::class.java,
            TombstoneMessage::class.java
        )
        return channel
    }

    @Bean
    fun batchedFileChannel() = PublishSubscribeChannel()

    @Bean
    fun aggregatorFlow(
        fileChannel: MessageChannel,
        batchedFileChannel: MessageChannel,
        correlationStrategy: FileBatchCorrelationStrategy
    ): IntegrationFlow {

        return IntegrationFlows.from(fileChannel)
            .aggregate { a: AggregatorSpec ->
                a.correlationStrategy ( correlationStrategy)
                    .releaseStrategy { messageGroup ->

                        val messages = messageGroup.messages
                        println("messages = ${messages.size}")
                        messages.firstOrNull { (it.payload is TombstoneMessage) } != null

                    }
            }.channel(batchedFileChannel)
            .handle<List<*>> { payload: Any, headers: MessageHeaders ->
                println("Aggregator!")
                payload as List<*> // this is a list of the payload objects from aggregated messages
                payload.forEach { message ->
                    println(message)
                }

            }
            .get()

    }

}

@Component
class FileBatchCorrelationStrategy : CorrelationStrategy {

    override fun getCorrelationKey(message: Message<*>?): Any? {
        return when (val payload = message!!.payload) {
            is FileMessage -> {
                payload.batchId.toString()
            }
            is TombstoneMessage -> {
                payload.batchId.toString()
            }
            else -> throw IllegalStateException("Unexpected class: ${payload::javaClass}")
        }
    }
}


@Component
class AggregatorMessageSource(@Qualifier("aggregatorFileChannel") private val fileChannel: MessageChannel) {

    fun produceMessages(count: Int) {

        val batchId = UUID.randomUUID()
        val tombstoneMessage = TombstoneMessage(batchId)

        for (i in 1..count) {
            val fileMessage = FileMessage(batchId)
            fileChannel.send(GenericMessage(fileMessage))
            tombstoneMessage.fileIds.add(fileMessage.id)
        }

        fileChannel.send(GenericMessage(tombstoneMessage))

    }

}


@Controller
class AggregatorExampleController(private val messageSource: AggregatorMessageSource) {

    @GetMapping("example/aggregator")
    fun aggregator() {
        messageSource.produceMessages(5)
    }

}