package io.micronaut.servlet.undertow

import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.reactivex.Single
import spock.lang.Issue
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest
@Property(name = 'spec.name', value = 'UndertowContentTypeSpec')
@Issue('https://github.com/micronaut-projects/micronaut-servlet/issues/206')
class UndertowContentTypeSpec extends Specification {

    @Inject
    @Client('/contentType')
    RxHttpClient client

    void 'test that method returning String without @Produces will have JSON response content-type'() {
        when:
        def response = client.exchange(
                HttpRequest.POST('/default/simple', 'foobar'), String
        ).blockingFirst()

        then:
        response.contentType.isPresent()
        response.contentType.get() == MediaType.APPLICATION_JSON_TYPE
        response.body() == 'Body: foobar'
    }

    void 'test that method returning HttpResponse without @Produces will have JSON response content-type'() {
        when:
        def response = client.exchange(
                HttpRequest.POST('/default/response', 'foobar'), String
        ).blockingFirst()

        then:
        response.contentType.isPresent()
        response.contentType.get() == MediaType.APPLICATION_JSON_TYPE
        response.body() == 'Body: foobar'
    }

    void 'test that method returning Single without @Produces will have JSON response content-type'() {
        when:
        def response = client.exchange(
                HttpRequest.POST('/default/reactive', 'foobar'), String
        ).blockingFirst()

        then:
        response.contentType.isPresent()
        response.contentType.get() == MediaType.APPLICATION_JSON_TYPE
        response.body() == 'Body: foobar'
    }

    void 'test that method returning String with @Produces TEXT_PLAIN will have text response content-type'() {
        when:
        def response = client.exchange(
                HttpRequest.POST('/plainText/simple', 'foobar'), String
        ).blockingFirst()

        then:
        response.contentType.isPresent()
        response.contentType.get() == MediaType.TEXT_PLAIN_TYPE
        response.body() == 'Body: foobar'
    }

    void 'test that method returning HttpResponse with @Produces TEXT_PLAIN will have text response content-type'() {
        when:
        def response = client.exchange(
                HttpRequest.POST('/plainText/response', 'foobar'), String
        ).blockingFirst()

        then:
        response.contentType.isPresent()
        response.contentType.get() == MediaType.TEXT_PLAIN_TYPE
        response.body() == 'Body: foobar'
    }

    void 'test that method returning Single with @Produces TEXT_PLAIN will have text response content-type'() {
        when:
        def response = client.exchange(
                HttpRequest.POST('/plainText/reactive', 'foobar'), String
        ).blockingFirst()

        then:
        response.contentType.isPresent()
        response.contentType.get() == MediaType.TEXT_PLAIN_TYPE
        response.body() == 'Body: foobar'
    }

    @Requires(property = 'spec.name', value = 'UndertowContentTypeSpec')
    @Controller('/contentType/default')
    static class DefaultContentTypeController extends ContentTypeControllerBase {
        @Post('/simple')
        String simple(@Body String text) {
            result(text)
        }
        @Post('/response')
        HttpResponse<String> response(@Body String text) {
            HttpResponse<String>.ok(result(text))
        }
        @Post('/reactive')
        Single<String> reactive(@Body String text) {
            Single<String>.just(result(text))
        }
    }

    @Requires(property = 'spec.name', value = 'UndertowContentTypeSpec')
    @Controller('/contentType/plainText')
    @Produces(MediaType.TEXT_PLAIN)
    static class PlainTextContentTypeController extends ContentTypeControllerBase {
        @Post('/simple')
        String simple(@Body String text) {
            result(text)
        }
        @Post('/response')
        HttpResponse<String> response(@Body String text) {
            HttpResponse<String>.ok(result(text))
        }
        @Post('/reactive')
        Single<String> reactive(@Body String text) {
            Single<String>.just(result(text))
        }
    }

    private static class ContentTypeControllerBase {
        @SuppressWarnings('GrMethodMayBeStatic')
        String result(String text) {
            "Body: $text"
        }
    }

}
