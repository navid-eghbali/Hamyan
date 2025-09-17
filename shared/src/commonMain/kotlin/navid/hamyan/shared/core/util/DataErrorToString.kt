package navid.hamyan.shared.core.util

import navid.hamyan.shared.core.domain.DataError
import navid.hamyan.shared.resources.Res
import navid.hamyan.shared.resources.error_disk_full
import navid.hamyan.shared.resources.error_insufficient_balance
import navid.hamyan.shared.resources.error_no_internet
import navid.hamyan.shared.resources.error_request_timeout
import navid.hamyan.shared.resources.error_serialization
import navid.hamyan.shared.resources.error_too_many_requests
import navid.hamyan.shared.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

fun DataError.toUiText(): StringResource = when (this) {
    DataError.Local.DISK_FULL -> Res.string.error_disk_full
    DataError.Local.INSUFFICIENT_FUNDS -> Res.string.error_insufficient_balance
    DataError.Local.UNKNOWN -> Res.string.error_unknown
    DataError.Remote.NO_INTERNET -> Res.string.error_no_internet
    DataError.Remote.REQUEST_TIMEOUT -> Res.string.error_request_timeout
    DataError.Remote.SERIALIZATION -> Res.string.error_serialization
    DataError.Remote.SERVER -> Res.string.error_unknown
    DataError.Remote.TOO_MANY_REQUESTS -> Res.string.error_too_many_requests
    DataError.Remote.UNKNOWN -> Res.string.error_unknown
}
