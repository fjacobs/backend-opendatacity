package com.dynacore.livemap.service;

import com.dynacore.livemap.entity.hibernate.ParkingLogData;
import com.dynacore.livemap.entity.jsonrepresentations.FeatureCollection;
import com.dynacore.livemap.repository.JpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

class ParkingPlaceCollectorServiceImplTest {

    private ParkingPlaceCollectorServiceImpl subject;

    @Mock
    private JpaRepository<ParkingLogData> parkingRepo;

    @Mock
    private FeatureCollection liveData;

    @BeforeEach
    void setUp() {
        initMocks(this);
        subject = new ParkingPlaceCollectorServiceImpl(parkingRepo);
        liveData = new FeatureCollection();
        liveData.setType("FeatureCollection");
    }

//    @Test
//    public void shouldReturnFullNameOfAPerson() throws Exception {
//        Person peter = new Person("Peter", "Pan");
//        given(personRepo.findByLastName("Pan"))
//                .willReturn(Optional.of(peter));
//
//        String greeting = subject.hello("Pan");
//
//        assertThat(greeting, is("Hello Peter Pan!"));
//    }

    @Test
    void getLiveData() {

    }

    @Test
    void saveCollection() {
    }

    @AfterEach
    void tearDown() {
    }

}