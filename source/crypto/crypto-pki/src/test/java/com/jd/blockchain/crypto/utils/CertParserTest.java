package com.jd.blockchain.crypto.utils;

import com.jd.blockchain.crypto.utils.CertParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author zhanglin33
 * @title: CertParserTest
 * @description: TODO
 * @date 2019-05-13, 10:05
 */
public class CertParserTest {

    @Test
    public void parseTest() {
        CertParser parser = new CertParser();
        String issuerCert =
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIDzzCCAregAwIBAgIKUalCR1Mt5ZSK8jANBgkqhkiG9w0BAQUFADBZMQswCQYD\n" +
                "VQQGEwJDTjEwMC4GA1UEChMnQ2hpbmEgRmluYW5jaWFsIENlcnRpZmljYXRpb24g\n" +
                "QXV0aG9yaXR5MRgwFgYDVQQDEw9DRkNBIFRFU1QgQ1MgQ0EwHhcNMTIwODI5MDU1\n" +
                "NDM2WhcNMzIwODI0MDU1NDM2WjBZMQswCQYDVQQGEwJDTjEwMC4GA1UEChMnQ2hp\n" +
                "bmEgRmluYW5jaWFsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MRgwFgYDVQQDEw9D\n" +
                "RkNBIFRFU1QgT0NBMTEwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC8\n" +
                "jn0n8Fp6hcRdACdn1+Y6GAkC6KGgNdKyHPrmsdmhCjnd/i4qUFwnG8cp3D4lFw1G\n" +
                "jmjSO5yVYbik/NbS6lbNpRgTK3fDfMFvLJpRIC+IFhG9SdAC2hwjsH9qTpL9cK2M\n" +
                "bSdrC6pBdDgnbzaM9AGBF4Y6vXhj5nah4ZMsBvDp19LzXjlGuTPLuAgv9ZlWknsd\n" +
                "RN70PIAmvomd10uqX4GIJ4Jq/FdKXOLZ2DNK/NhRyN6Yq71L3ham6tutXeZow5t5\n" +
                "0254AnUlo1u6SeH9F8itER653o/oMLFwp+63qXAcqrHUlOQPX+JI8fkumSqZ4F2F\n" +
                "t/HfVMnqdtFNCnh5+eIBAgMBAAGjgZgwgZUwHwYDVR0jBBgwFoAUdN7FjQp9EBqq\n" +
                "aYNbTSHOhpvMcTgwDAYDVR0TBAUwAwEB/zA4BgNVHR8EMTAvMC2gK6AphidodHRw\n" +
                "Oi8vMjEwLjc0LjQyLjMvdGVzdHJjYS9SU0EvY3JsMS5jcmwwCwYDVR0PBAQDAgEG\n" +
                "MB0GA1UdDgQWBBT8C7xEmg4xoYOpgYcnHgVCxr9W+DANBgkqhkiG9w0BAQUFAAOC\n" +
                "AQEAb7W0K9fZPA+JPw6lRiMDaUJ0oh052yEXreMBfoPulxkBj439qombDiFggRLc\n" +
                "3g8wIEKzMOzOKXTWtnzYwN3y/JQSuJb/M1QqOEEM2PZwCxI4AkBuH6jg03RjlkHg\n" +
                "/kTtuIFp9ItBCC2/KkKlp0ENfn4XgVg2KtAjZ7lpyVU0LPnhEqqUVY/xthjlCSa7\n" +
                "/XHNStRxsfCTIBUWJ8n2FZyQhfV/UkMNHDBIiJR0v6C4Ai0/290WvbPEIAq+03Si\n" +
                "fsHzBeA0C8lP5VzfAr6wWePaZMCpStpLaoXNcAqReKxQllElOqAhRxC5VKH+rnIQ\n" +
                "OMRZvB7FRyE9IfwKApngcZbA5g==\n" +
                "-----END CERTIFICATE-----";

        String userCert = "MIIEQDCCAyigAwIBAgIFICdVYzEwDQYJKoZIhvcNAQEFBQAwWTELMAkGA1UEBhMCQ04xMDAuBgNVBAoTJ0NoaW5hIEZpbmFuY2lhbCBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTEYMBYGA1UEAxMPQ0ZDQSBURVNUIE9DQTExMB4XDTE5MDUxMDExMjAyNFoXDTIxMDUxMDExMjAyNFowcjELMAkGA1UEBhMCQ04xGDAWBgNVBAoTD0NGQ0EgVEVTVCBPQ0ExMTERMA8GA1UECxMITG9jYWwgUkExFTATBgNVBAsTDEluZGl2aWR1YWwtMTEfMB0GA1UEAxQWMDUxQGFhYWFhQFpIMDkzNTgwMjhAMzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAJx3F2WD1dJPzK/nRHO7d1TJ1hTjzGTmv0PQ7ECsJAh3U3BtnGTpCB+b4+JMI4LO8nHkKIBQ3P9XnF+Bf1iXdWNAQ4aWCxa2nV7lCp4w0GliPu/EMgIfmsSDUtgqbM3cr8sR8r9m1xG3gt2TIQJ+jT7sAiguU/kyNzpjaccOUIgUFa8IDFq9UeB76MXtCuhlERRZQCl47e+9w7ZoxmE7e6IZORxPp7rQWVBHlR9ntWjJfNDTm3gMP5ehP+yIZnKx1LudxkBLQxpMmspzOyH1zqx5nkKe49AfWWpDxxRvYkriyYC3aE81qLsU/bhLwNEKOju7BGDF/mhJLZUedojM0gMCAwEAAaOB9TCB8jAfBgNVHSMEGDAWgBT8C7xEmg4xoYOpgYcnHgVCxr9W+DBIBgNVHSAEQTA/MD0GCGCBHIbvKgECMDEwLwYIKwYBBQUHAgEWI2h0dHA6Ly93d3cuY2ZjYS5jb20uY24vdXMvdXMtMTUuaHRtMDoGA1UdHwQzMDEwL6AtoCuGKWh0dHA6Ly8yMTAuNzQuNDIuMy9PQ0ExMS9SU0EvY3JsMjU2OTMuY3JsMAsGA1UdDwQEAwID6DAdBgNVHQ4EFgQU5oKGaQs7Jt5Gfbt1XhFTWAySEKswHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMEMA0GCSqGSIb3DQEBBQUAA4IBAQAlmPRaImZV51iKjtpMKuyLMw7dX8L0lY3tl+pVZZSxHuwsN4GCCtV0Ej50up+/6EbfL4NUTiuHVAjCroKKvb+94CrdEwdnQGM5IbGSjT78nQpeASXbIWuUwA+ImjvZOzvq/0b56AzonNzBxOMGko/bj5smM6X8jrgJ0NQppo2KNSVNC4JbuoNWI4FM94SE4DUi9H7EYl4JdOtDaDtCsq49o/A1CZyYrmoOPCgxpQQXmuB3lGq/jyoOlW2aW8uee/hYG1JJcSHLBjF0WBwdxssgbBotA5f1PebiIMSbFgjk57bd4M80hhU/rI4Hkn9pcp5R7NsX95TtyDIg90LboBnW";

        parser.parse(userCert, issuerCert);
        assertEquals("SHA1WITHRSA",parser.getSigAlgName());
    }
}
