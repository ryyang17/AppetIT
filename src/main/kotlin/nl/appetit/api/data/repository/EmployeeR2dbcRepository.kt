package nl.appetit.api.data.repository

import nl.appetit.api.data.entity.EmployeeEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface EmployeeR2dbcRepository : ReactiveCrudRepository<EmployeeEntity, Int>
