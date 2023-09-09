package org.microjava.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.microjava.business.AccountProcessor;
import org.microjava.model.entities.Account;
import org.microjava.model.entities.AccountDetails;
import org.microjava.rest.query.AccountQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * The secondary REST controller for micro-service.
 * 
 * @author  Gaurav J
 * @since   10 May 19
 * @version 1.0
 */

@Tag(name = "Endpoints")
@Component
@Path("/v3/account")
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationService {

	private final Logger log = LoggerFactory.getLogger(ApplicationService.class);

	@Autowired
	private AccountProcessor processor;

	@Operation(
        description = "The endpoint to fetch all accounts", 
        responses = {
            @ApiResponse(
            	responseCode = "200",
                content = @Content(schema = @Schema(implementation = List.class))
            )
        }
	)
	@GET
	@Path("/list")
	public ResponseEntity<List<Account>> accounts() { 
		Optional<List<Account>> result = Optional.<List<Account>>of(processor.findAll());
		
		List<Account> accounts = result.isPresent() ? result.get() : new ArrayList<Account>();
		
		return ResponseEntity.ok(accounts);
	}
	
	@Operation(
        description = "The endpoint retrives a single account", 
        responses = {
            @ApiResponse(
            	responseCode = "200",
                content = @Content(schema = @Schema(implementation = Account.class))
            )
        }
	)
	@GET
	@Path("/id/{id}")
	public ResponseEntity<Account> account(@PathParam("id") Long id) {
		Optional<Account> result = Optional.<Account>of(processor.findById(id));
		
		Account account = result.isPresent() ? result.get() : new Account();
		account = account.withReason("urgent purchase").with("p", "one");
		
		return ResponseEntity.ok(account);
	}
	
	@Operation(
        description = "The endpoint retrives an account by example", 
        responses = {
            @ApiResponse(
            	responseCode = "200",
                content = @Content(schema = @Schema(implementation = List.class))
            )
        }
	)
	@POST
	@Path("/find")
	@Consumes(MediaType.APPLICATION_JSON)
	public ResponseEntity<List<Account>> find(AccountQuery query) {
		Optional<List<Account>> result = Optional.<List<Account>>of(processor.findAccounts(query));
		
		List<Account> accounts = result.isPresent() ? result.get() : new ArrayList<Account>();
		
		return ResponseEntity.ok(accounts);
	}
	
	@Operation(description = "The endpoint credits an account by query", responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = List.class))),
			@ApiResponse(responseCode = "400", description = "The request was invalid.") })
	@POST
	@Path("/credit")
	@Consumes(MediaType.APPLICATION_JSON)
	public ResponseEntity<String> credit(AccountQuery query) {
		if(query.getAmount() == 0 || null == query.getName() || query.getName().isEmpty()) {
			return ResponseEntity.badRequest().body("Bad request");
		}
		if(null == query.getUser() || query.getUser().isEmpty()) {
			return ResponseEntity.badRequest().body("Bad request");
		}
		
		String user = query.getUser();
		query.setUser(null);
		
		Optional<List<Account>> result = Optional.<List<Account>>of(processor.findAccounts(query));
		List<Account> accounts = result.isPresent() ? result.get() : new ArrayList<Account>();

		double credits = accounts.stream().filter(a -> a.getType().equals("C")).map(a -> a.getAmount()).reduce(0.0, Double::sum);
		double debits = accounts.stream().filter(a -> a.getType().equals("D")).map(a -> a.getAmount()).reduce(0.0, Double::sum);
		
		Account acct = new Account();
		if(query.getAmount() > 0) {
			acct.setName(query.getName());
			acct.setUser(user);
			acct.setAmount(query.getAmount());
			acct.setUpdated("23-08-2019 09:00");
			
			acct.setBalance(credits+query.getAmount() - debits);
			credits = credits + query.getAmount();
			
			processor.save(acct);	
		}
		else {
			return ResponseEntity.badRequest().body("The amount to deposit shall be more thean 0.0 Rs.");
		}
		
		return ResponseEntity.ok(String.format("The balance in your account is %1$,.2f Rs.", (credits-debits)));
	}
	
	@Operation(description = "The endpoint debits an account by query", responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = List.class))),
			@ApiResponse(responseCode = "400", description = "The request was invalid.") })
	@POST
	@Path("/debit")
	@Consumes(MediaType.APPLICATION_JSON)
	public ResponseEntity<String> debit(AccountQuery query) {
		if(query.getAmount() == 0 || null == query.getName() || query.getName().isEmpty()) {
			return ResponseEntity.badRequest().body("Bad request");
		}
		if(null == query.getUser() || query.getUser().isEmpty()) {
			return ResponseEntity.badRequest().body("Bad request");
		}
		
		String user = query.getUser();
		query.setUser(null);
		
		Optional<List<Account>> result = Optional.<List<Account>>of(processor.findAccounts(query));
		List<Account> accounts = result.isPresent() ? result.get() : new ArrayList<Account>();
		
		double credits = accounts.stream().filter(a -> a.getType().equals("C")).map(a -> a.getAmount()).reduce(0.0, Double::sum);
		double debits = accounts.stream().filter(a -> a.getType().equals("D")).map(a -> a.getAmount()).reduce(0.0, Double::sum);
		
		Account acct = new Account();
		if(credits >= (debits + query.getAmount())) {
			acct.setName(query.getName());
			acct.setUser(user);
			acct.setAmount(query.getAmount());
			acct.setUpdated("23-08-2019 09:00");
			
			acct.setBalance(credits - (debits+query.getAmount()));
			debits = debits + query.getAmount();
			
			processor.save(acct);	
		}
		else {
			return ResponseEntity.badRequest().body(String.format("Insufficient funds, the current balance in your account is %1$,.2f Rs.", (credits-debits)));
		}
		
		return ResponseEntity.ok(String.format("The current balance in your account is %1$,.2f Rs.", (credits-debits)));
	}
	
	@Operation(
        description = "The endpoint retrives an account by name", 
        responses = {
            @ApiResponse(
            	responseCode = "200",
                content = @Content(schema = @Schema(implementation = List.class))
            )
        }
	)
	@GET
	@Path("/name/{name}")
	public ResponseEntity<List<Account>> name(@PathParam("name") String name) {
		Optional<List<Account>> result = Optional.<List<Account>>of(processor.findByName(name));
		
		List<Account> accounts = result.isPresent() ? result.get() : new ArrayList<Account>();
		
		return ResponseEntity.ok(accounts);
	}
	
	@Operation(
        description = "The endpoint retrives the details of an account", 
        responses = {
            @ApiResponse(
            	responseCode = "200",
                content = @Content(schema = @Schema(implementation = List.class))
            ),
            @ApiResponse(
            	responseCode = "404",
            	description = "The resource was not found."
            ),
            @ApiResponse(
            	responseCode = "400",
            	description = "The request was invalid."
            )
        }
	)
	@GET
	@Path("/{id}")
	public ResponseEntity<List<AccountDetails>> users(@PathParam("id") Long id) {
		log.debug("Inside to fecth the user details.");
		Optional<List<AccountDetails>> result = Optional.<List<AccountDetails>>of(processor.findAllById(id));
		
		List<AccountDetails> accountDetails = result.isPresent() ? result.get() : new ArrayList<AccountDetails>();

		return ResponseEntity.ok(accountDetails);
	}
	
	@Operation(
        description = "The endpoint retrives the history of an account", 
        responses = {
            @ApiResponse(
            	responseCode = "200",
                content = @Content(schema = @Schema(implementation = Map.class))
            ),
            @ApiResponse(
            	responseCode = "404",
            	description = "The resource was not found."
            ),
            @ApiResponse(
            	responseCode = "400",
            	description = "The request was invalid."
            )
        }
	)
	
	@GET
	@Path("/history/{id}")
	public ResponseEntity<Map<String, Map<String, List<AccountDetails>>>> memberships(@PathParam("id") Long id) {
		Optional<Map<String, Map<String, List<AccountDetails>>>> result = Optional.<Map<String, Map<String, List<AccountDetails>>>>of(processor.findHistoryById(id));

		Map<String, Map<String, List<AccountDetails>>> accountDetails = result.isPresent() ? result.get() : new HashMap<String, Map<String,List<AccountDetails>>>();

		return new ResponseEntity<>(accountDetails, HttpStatus.OK);
	}
	
	@Operation(
        description = "The endpoint provides a JWT token", 
        responses = {
            @ApiResponse(
            	responseCode = "200",
                content = @Content(schema = @Schema(implementation = String.class))
            )
        }
	)
	
	@POST
	@Path("/build-claims")
	@Consumes(MediaType.APPLICATION_JSON)
	public ResponseEntity<String> dynamicBuilderSpecific(Map<String, String> claims){
		JwtBuilder builder = Jwts.builder();
		
		claims.forEach((key, value)->{
			
			switch (key) {
				case "issuer":
					builder.setIssuer(value);
					break;
				case "subject":
					builder.setSubject(value);
					break;
				case "audience":
					builder.setAudience(value);
					break;
				case "expiration":
					builder.setExpiration(Date.from(
						Instant.ofEpochSecond(Long.parseLong(value))
					));
					break;
				case "notBefore":
					builder.setNotBefore(Date.from(
						Instant.ofEpochSecond(Long.parseLong(value))
					));
					break;
				case "issuedAt":
					builder.setIssuedAt(Date.from(
						Instant.ofEpochSecond(Long.parseLong(value))
					));
					break;
				case "id":
					builder.setId(value);
					break;
				default:
					builder.claim(key, value);
			}
		});
		
		builder.signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256));
		
		return new ResponseEntity<>(builder.compact(), HttpStatus.OK);
	}
}

/*
 * public static void handleJwt() throws NoSuchAlgorithmException,
 * InvalidKeySpecException { //Key key =
 * Keys.secretKeyFor(SignatureAlgorithm.HS256); KeyPair keyPair =
 * Keys.keyPairFor(SignatureAlgorithm.RS256); String jws =
 * Jwts.builder().setSubject("jwt-token")
 * .setIssuer("Gaurav").setAudience("team").setIssuer("gaurav")
 * .setExpiration(Date.valueOf(LocalDate.now().plusDays(1)))
 * .signWith(keyPair.getPrivate()).compact(); try(OutputStream writer =
 * Files.newOutputStream(Paths.get("files/public_id"),
 * StandardOpenOption.CREATE_NEW)){
 * writer.write(keyPair.getPublic().getEncoded()); writer.flush(); }
 * catch(Exception e) { System.out.println(e.getMessage()); }
 * //HBxl9mAe6gxavCkcoOU2THsDNa0 String publicKey = "B@543c6f6d"; byte[] res
 * = DatatypeConverter.parseBase64Binary(publicKey); KeyFactory keyFactory =
 * KeyFactory.getInstance("RSA"); X509EncodedKeySpec KeySpec = new
 * X509EncodedKeySpec(res); RSAPublicKey pubKey = (RSAPublicKey)
 * keyFactory.generatePublic(KeySpec); Claims claims =
 * Jwts.parser().setSigningKey(keyPair.getPublic()).parseClaimsJws(jws)
 * .getBody();
 * System.out.println(claims.getIssuer()+" --> "+claims.getSubject()); }
 * public static final String payload() { return
 * "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkhCeGw5bUFlNmd4YXZDa2NvT1UyVEhzRE5hMCIsImtpZCI6IkhCeGw5bUFlNmd4YXZDa2NvT1UyVEhzRE5hMCJ9.eyJhdWQiOiI4YzZkZjg3NS05OTAxLTQzNGYtODQyYS05NDVjYWJiOTliMTUiLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC9mOTdkN2ZlYy1hMTNhLTQ0OGEtYjcwNC0wNGNkN2UxYjdjMzkvIiwiaWF0IjoxNTU2NzI0OTk5LCJuYmYiOjE1NTY3MjQ5OTksImV4cCI6MTU1NjcyODg5OSwiYWlvIjoiQVZRQXEvOExBQUFBcUhTc2xtZU5qTzVFV2pVQ2IwVk9welpaMHJkSE0vYmNRTVVVcHZ3OFZUOWU2Y1R5SExROEN5SE5rZDBDRGl1clVSeWZjeHJjeVFqT2dXcmJNdTN1WDBhcU5YaTlYN3Y4V1JxNmU3ZlRoRm89IiwiYW1yIjpbInB3ZCIsIm1mYSJdLCJmYW1pbHlfbmFtZSI6IkphZGhhdiIsImdpdmVuX25hbWUiOiJQcmFzYWQiLCJncm91cHMiOlsiNzg0ZTQ3Y2MtZTc2Yi00MDY1LWFjMWUtYzU1MDVjMGQ0N2I2IiwiNTY5YTJhMmQtYmE4ZC00ZDk4LWFhODItYWE1YmY4ZGNkNGEzIiwiMDZiYTI1ZGUtNDI5Yi00ZDcwLWE2YWQtOGRjNTMwN2IxNDNmIiwiYTdiNzgyMGEtYzE5My00NjA4LWI3MTYtNzJjNTJiNGMxNDRkIl0sImlwYWRkciI6IjUxLjE0NC4xMjYuOTUiLCJuYW1lIjoiUHJhc2FkIEphZGhhdiIsIm5vbmNlIjoiNzQ2NWUxMmMtMjljYi00YjhmLWJiNjUtYTc0NjNlOTdjODIzIiwib2lkIjoiNThlMmNiNWItYTdhMS00ZjgwLWI1NDYtNmY2OGI2NWE1MWViIiwic3ViIjoiLWZDeU9rLU02SDR6TUpZWVBmMWVjVGJad2gzTmRqX2Z0WURLQ1hyR0xucyIsInRpZCI6ImY5N2Q3ZmVjLWExM2EtNDQ4YS1iNzA0LTA0Y2Q3ZTFiN2MzOSIsInVuaXF1ZV9uYW1lIjoiamFkaGF2cEBhei5lYnJkLmNvbSIsInVwbiI6ImphZGhhdnBAYXouZWJyZC5jb20iLCJ1dGkiOiJoSm5vb3RybWRrcUxXeldfek1CTkFBIiwidmVyIjoiMS4wIn0.LFE5MJHUkIxvxsWfBlNdHZKlChTBejz_k9L9h0MgrygF34iOD3vpEGnoN9UGN0jXO-7-mMnTTq01HIxftgKKFXjNwIAFCNHFZm-1gQzaCFhcOGoE4LsPfJJ5DFtJjkVl-qz139S7l2YLYrVQHIPysY4nx-ra3gb39j2dFH80MAIMABsGYz1pVPHYlTmh0FDMT1FEjPz0R6kL6CXMiMJU2-XBMh0ktmYj9eq3CAwZSBxuY4NorubfbQ5HJMz29Dq5aFqlyJpaB0TxpDgOUbAWiz-lX3_PsMuc09TQJ2UX0HecvheoAWA540U4RwfeC3MoTjGrXvcFiTZr7Za4uPX-qg";
 * }
 */