package blended.samples

import blended.ui.samples.JsonWebToken
import org.scalatest.FreeSpec

class JWtDecodeSpec extends FreeSpec {

  // sample token generated with the blended permission API
  val token = "eyJhbGciOiJSUzUxMiJ9.eyJqdGkiOiIyMDE4LTA4LTE1LTE5OjQ3OjQ0OjQ4OS0xIiwic3ViIjoiYW5kcmVhcyIsImlhdCI6MTUzNDM1NTI2NCwicGVybWlzc2lvbnMiOiJ7XCJncmFudGVkXCI6IHtcIiNlbGVtc1wiOiBbe1wicGVybWlzc2lvbkNsYXNzXCI6IHtcIiNlbGVtc1wiOiBbXCJhZG1pbnNcIl19LCBcInByb3BlcnRpZXNcIjoge1wiI2VsZW1zXCI6IFtdfX0sIHtcInBlcm1pc3Npb25DbGFzc1wiOiB7XCIjZWxlbXNcIjogW1wiYmxlbmRlZFwiXX0sIFwicHJvcGVydGllc1wiOiB7XCIjZWxlbXNcIjogW119fV19fSIsImV4cCI6MTUzNDM1NTMyNH0.ObRwVtt2XlA_WRGtcVwhr_jOm1xvzOQUlsvqXu7RMN-j7hqdWp-eqkwjC6OL0jL7iXKTDw3I9ZBz4AvJpUYgsn5YoTbfs5L_5Iqe1F4mh9Pcp4VSN9F9Tuhh5YufEdN1F-YO2AssPC1fYWiW1cBEgqXGY91IVY_p6hHBOdPRPfCC-hLucXtRyzsyK8e3FcvOL-juhbDuY9Nef2E-160AS7Wl-hdEkOretdqMPZYnJxO3eUtiDyQtSU1GiBp8AuhsferqLp6LtHF6hDxq0o7k3_3vbcNR1OSTz9SXl8JJylG2XkcpeBXjpsy4Gc1SRikGPyfDcPKIt8fPH1IrAEoOkw"

  "A Json Web token" - {

    "should decode correctly" in {
      val foo = JsonWebToken.decode(token)
      foo.keys.foreach(println)
      foo.get("permissions").foreach(println)
    }
  }


}
