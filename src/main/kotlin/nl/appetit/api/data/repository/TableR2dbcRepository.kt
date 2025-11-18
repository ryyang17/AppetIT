// ...new file...
package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.TableEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TableR2dbcRepository : ReactiveCrudRepository<TableEntity, Int> {

}

