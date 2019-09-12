package com.dynacore.livemap.service;

import com.dynacore.livemap.common.model.FeatureCollection;
import com.dynacore.livemap.common.repo.JpaRepository;
import com.dynacore.livemap.parking.ParkingDTO;
import com.dynacore.livemap.parking.ParkingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

public class ParkingPlaceModelCollectorServiceImplTest {

    private ParkingService subject;

    @Mock
    private JpaRepository<ParkingDTO> parkingRepo;

    @Mock
    private FeatureCollection liveData;

    @BeforeEach
    void setUp() {
        initMocks(this);
        subject = new ParkingService(parkingRepo);
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