package com.porsche.digital.cities.errors;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author Josip Primorac (josip.primorac@ecx.io)
 */
public class EntityExistsException extends RuntimeException
{
    public EntityExistsException(Class clazz, String... paramsMap)
    {
        super(EntityExistsException.generateMessage(
                clazz.getSimpleName(),
                toMap(String.class, String.class, paramsMap)
        ));
    }

    private static String generateMessage(String entity, Map<String, String> params)
    {
        return StringUtils.capitalize(entity) +
                " was already exists for parameters " +
                params;
    }

    private static <K, V> Map<K, V> toMap(
            Class<K> keyType, Class<V> valueType, Object... entries
    )
    {
        if (entries.length % 2 == 1)
        { throw new IllegalArgumentException("Invalid entries"); }
        return IntStream.range(0, entries.length / 2).map(i -> i * 2)
                        .collect(
                                HashMap::new,
                                (m, i) -> m.put(keyType.cast(entries[i]), valueType.cast(entries[i + 1])),
                                Map::putAll
                        );
    }
}
