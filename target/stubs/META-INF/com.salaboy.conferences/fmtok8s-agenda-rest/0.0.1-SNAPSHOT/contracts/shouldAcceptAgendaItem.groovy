import org.springframework.cloud.contract.spec.Contract
[
        Contract.make {
            name "should accept POST with new Proposal"
            request{
                method 'POST'
                url '/'
                body([
                        "title": $(anyNonEmptyString()),
                        "author": $(anyNonEmptyString()),
                        "day": $(anyNonEmptyString()),
                        "time": $(anyNonEmptyString())
                ])
                headers {
                    contentType('application/json')
                }
            }
            response {
                status OK()
                headers {
                    contentType('text/plain')
                }
                body("Agenda Item Added to Agenda")
            }
        }

]