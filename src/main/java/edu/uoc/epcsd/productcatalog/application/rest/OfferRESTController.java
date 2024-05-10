package edu.uoc.epcsd.productcatalog.application.rest;


import edu.uoc.epcsd.productcatalog.application.rest.request.AddOfferRequest;
import edu.uoc.epcsd.productcatalog.domain.Offer;
import edu.uoc.epcsd.productcatalog.domain.service.CategoryNotFoundException;
import edu.uoc.epcsd.productcatalog.domain.service.OfferService;
import edu.uoc.epcsd.productcatalog.domain.service.ProductNotFoundException;
import edu.uoc.epcsd.productcatalog.domain.service.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
@RequestMapping("/offers")
public class OfferRESTController {

    private final OfferService offerService;

    @PostMapping
    public ResponseEntity<Long> addOffer(@RequestBody @NotNull @Valid AddOfferRequest addOfferRequest) {
        log.trace("addOffer");

        log.trace("Creating offer " + addOfferRequest);

        try {
            Long offerId = offerService.addOffer(Offer.builder()
                    .email(addOfferRequest.getEmail())
                    .categoryId(addOfferRequest.getCategoryId())
                    .productId(addOfferRequest.getProductId())
                    .serialNumber(addOfferRequest.getSerialNumber())
                    .build());

            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(offerId)
                    .toUri();

            return ResponseEntity.created(uri).body(offerId);
        } catch (UserNotFoundException | CategoryNotFoundException | ProductNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The specified product category " + addOfferRequest.getCategoryId() + " does not exist.", e);
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Offer> findOffersByUser(@RequestParam @NotNull String email) {
        log.trace("findOffersByUser");

        return offerService.findOffersByUser(email);
    }
}
