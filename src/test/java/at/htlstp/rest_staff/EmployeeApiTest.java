package at.htlstp.rest_staff;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class Saving {

        @Test
        @DirtiesContext
            // nur beim Verändern von Daten anzuwenden
        void works() throws Exception {
            var json = """
                    {
                       "id": "NEU",
                       "firstName": "Neu",
                       "lastName": "Neu"
                    }
                    """;
            var resource = "/api/employees/NEU";

            mockMvc.perform(post("/api/employees")
                            .contentType(APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost" + resource))
                    .andExpect(content().json(json)).andReturn();
            mockMvc.perform(get(resource))
                    .andExpect(status().isOk())
                    .andExpect(content().json(json));
        }

        @Test
        @DirtiesContext
        void updatesEmployeeIfAlreadyExists() throws Exception {
            var json = """
                    {
                       "id": "HUBE",
                       "firstName": "Neu",
                       "lastName": "Neu"
                    }
                    """;
            var resource = "/api/employees/HUBE";

            mockMvc.perform(post("/api/employees")
                            .contentType(APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost" + resource))
                    .andExpect(content().json(json)).andReturn();
            mockMvc.perform(get(resource))
                    .andExpect(status().isOk())
                    .andExpect(content().json(json));
        }



    }

    @Nested
    class Finding {
        @Test
        void works() throws Exception {
            var json = """
                    {
                       "id": "HUBE",
                       "firstName": "Franz",
                       "lastName": "Huber"
                    }
                    """;
            var resource = "/api/employees/HUBE";

            mockMvc.perform(get(resource))
                    .andExpect(status().isOk())
                    .andExpect(content().json(json));
        }

        @Test
        void returnStatusNotFoundIfEmployeeDoesNotExist() throws Exception {
            var resource = "/api/employees/TypDenEsNichtGibt";

            mockMvc.perform(get(resource))
                    .andExpect(status().isNotFound());
        }

        @Test
        void findByNameWorks() throws Exception {
            var resource = "/api/employees?name=Bar";

            var json = """
                    [
                        {"id":"SCMI","firstName":"Barbara","lastName":"Schmidt"}
                    ]
                    """;

            mockMvc.perform(get(resource))
                    .andExpect(content().json(json));
        }

        @Test
        void findByNameIgnoresCase() throws Exception {
            var resource = "/api/employees?name=bar";

            var json = """
                    [
                        {"id":"SCMI","firstName":"Barbara","lastName":"Schmidt"}
                    ]
                    """;

            mockMvc.perform(get(resource))
                    .andExpect(content().json(json));
        }

        @Test
        void findByNameReturnsEmptyIfNameDoesNotMatch() throws Exception {
            var resource = "/api/employees?name=moritz";

            var json = """
                    [
                        
                    ]
                    """;

            mockMvc.perform(get(resource))
                    .andExpect(content().json(json));
        }


    }

    @Nested
    class Tasks {
        @Test
        void hoursWorkedWorks() throws Exception {
            var resource = "/api/employees/HUBE/hoursWorked";


            mockMvc.perform(get(resource))
                    .andExpect(status().isOk())
                    .andExpect(content().json("480"));
        }

        @Test
        void tasksBetweenShowsAllTasksIfNoArguments() throws Exception {
            var resource = "/api/employees/HUBE/tasks";

            var json = """
                    [
                        {"id":1,"description":"Implementierung JUnit Tests","employee":{"id":"HUBE","firstName":"Franz","lastName":"Huber"},"finished":"2019-05-17","hoursWorked":120},
                        {"id":3,"description":"Projektmeeting","employee":{"id":"HUBE","firstName":"Franz","lastName":"Huber"},"finished":"2019-05-18","hoursWorked":60},
                        {"id":8,"description":"Tests und Bugfixes","employee":{"id":"HUBE","firstName":"Franz","lastName":"Huber"},"finished":"2019-05-19","hoursWorked":300}
                    ]
                    """;


            mockMvc.perform(get(resource))
                    .andExpect(status().isOk())
                    .andExpect(content().json(json));
        }


        @Test
        void returnsZeroIfEmployeeHasNoTasks() throws Exception {
            var resource = "/api/employees/3/hoursWorked";

            mockMvc.perform(get(resource))
                    .andExpect(status().isOk())
                    .andExpect(content().json("0"));
        }

        @Test
        void returnsOnlyInRightTimespan() throws Exception {
            var resource = "/api/employees/HUBE/tasks?from=2019-05-19&to=2020-01-01";

            var json = """
                    [
                        {"id":8,"description":"Tests und Bugfixes","employee":{"id":"HUBE","firstName":"Franz","lastName":"Huber"},"finished":"2019-05-19","hoursWorked":300}
                    ]
                    """;

            mockMvc.perform(get(resource))
                    .andExpect(status().isOk())
                    .andExpect(content().json(json));
        }

        @Test
        void returnsEmptyIfNoTasksInPeriod() throws Exception {
            var resource = "/api/employees/HUBE/tasks?from=2019-05-20&to=2020-01-01";

            var json = """
                    []
                    """;

            mockMvc.perform(get(resource))
                    .andExpect(status().isOk())
                    .andExpect(content().json(json));
        }
    }
}
