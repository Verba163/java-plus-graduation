package kafka.desarialization;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

    private final Schema schema;
    private final Class<T> clazz;
    private final DatumReader<T> datumReader;

    public BaseAvroDeserializer(Class<T> clazz, Schema schema) {
        this.clazz = clazz;
        this.schema = schema;
        this.datumReader = new SpecificDatumReader<>(schema);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) return null;

        try {
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            T result = clazz.getDeclaredConstructor().newInstance();
            return datumReader.read(result, decoder);
        } catch (IOException | ReflectiveOperationException e) {
            throw new SerializationException("Deserialization error for " + clazz.getSimpleName(), e);
        }
    }
}