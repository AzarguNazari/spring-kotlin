package dashboard.exceptions

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase

class LowerCaseClassNameResolver : TypeIdResolverBase() {

    override fun idFromValue(value: Any): String {
        return value.javaClass.toString()
    }

    override fun idFromValueAndType(value: Any, suggestedType: Class<*>?): String {
        return idFromValue(value)
    }

    override fun getMechanism(): JsonTypeInfo.Id {
        return JsonTypeInfo.Id.CUSTOM
    }
}