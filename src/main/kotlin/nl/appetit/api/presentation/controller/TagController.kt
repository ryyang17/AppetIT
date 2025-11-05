package nl.appetit.api.presentation.controller

import nl.appetit.api.logic.service.TagService
import nl.appetit.api.presentation.dto.tag.TagRequest
import nl.appetit.api.presentation.dto.tag.TagResponse
import nl.appetit.api.presentation.mapper.TagMapper
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/tags")
class TagController(
    private val tagService: TagService
) {

    @GetMapping
    fun list(): Flux<TagResponse> =
        tagService.findAll().map(TagMapper::toResponse)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int): Mono<TagResponse> =
        tagService.findById(id).map(TagMapper::toResponse)

    @PostMapping
    fun insert(@RequestBody request: Mono<TagRequest>): Mono<TagResponse> {
        return request.flatMap { tagReq ->
            tagService.save(TagMapper.toModel(tagReq))
        }.map(TagMapper::toResponse)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody request: Mono<TagRequest>): Mono<TagResponse> {
        return request.flatMap { tagReq ->
            tagService.update(id, TagMapper.toModel(tagReq))
        }.map(TagMapper::toResponse)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): Mono<Void> =
        tagService.deleteById(id)
}
