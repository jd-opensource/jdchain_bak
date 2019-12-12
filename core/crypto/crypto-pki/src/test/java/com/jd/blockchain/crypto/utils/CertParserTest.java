package com.jd.blockchain.crypto.utils;

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
    public void parseSHA1WITHRSA2048Test() {
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
        assertEquals("SHA1WITHRSA", parser.getSigAlgName());
        assertEquals(2048, parser.getKeyLength());
    }

    @Test
    public void parseSHA1WITHRSA4096Test() {
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

        String userCert = "MIIFRjCCBC6gAwIBAgIFICdWiDMwDQYJKoZIhvcNAQEFBQAwWTELMAkGA1UEBhMCQ04xMDAuBgNVBAoTJ0NoaW5hIEZpbmFuY2lhbCBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTEYMBYGA1UEAxMPQ0ZDQSBURVNUIE9DQTExMB4XDTE5MDUxNjA3MDcyMloXDTIxMDUxNjA3MDcyMloweDELMAkGA1UEBhMCQ04xGDAWBgNVBAoTD0NGQ0EgVEVTVCBPQ0ExMTERMA8GA1UECxMITG9jYWwgUkExFTATBgNVBAsTDEluZGl2aWR1YWwtMTElMCMGA1UEAxQcMDUxQHpoYW5nbGluIUBaMTg2MTIyMjkyOTVAMjCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBAL0rTOxd8rsjPtFJ0aGVh9bZPy5Xo0SADaP7BbJsG4+ykLQMZHg9hTf/6fv1OsD2HEKFoMpIkW2gwCJW2nvicHcVql/shCoktc6ZBW6Dr/DxOgbO9tpoGxZ50xdI4Q0NsrxqtbCldW4ozPHdjgRJ83i1KSFh7evNrVByN/mB+jchrVGLWyJ1uIRgUUgpRZmZPoOHaizVJqrqWJGGk6xbDLR2gUQ1hTzetQaz1OeKtelHDk9FY08XSmNGssSMpuSjrxn78S888VW5WIxyo4cwrXSXFk3J7LNTy70Oga16HZjJD/vLTM6a4riPa8+uivPinKxK38/++nlBPNwhx6n46uYkd9Zvw+SJiJgpnuPJLtMZpKpJx7V1BDVEydKPUthilTdsmJtkBFSlFw0G1aKfuciBGzzJ3SKngJF/JqJAWIonVAFBGb6Gokp1Sw+T4KqXrdbjxYxiyyjZ++8O1vydgFAkx/NjsuwJnpKETiRKFJmY7YawcUvC4ixF7XQc0luFWRDYcbxOppir+ieMqhGXyaFhLUuB4WXv+rFxfa3NmkBW8q5TPzt/PwWcXpITsYTZYla/E/grB+OeZLYgjigT5YlgytPHG6Gt1ySCCd8WXFWpkBbQfXzqcvtU27RCcAUgfXk5NLb7NZCQg7heGjgzOdYJCPsa1d3m7l04+VIKGCZdAgMBAAGjgfUwgfIwHwYDVR0jBBgwFoAU/Au8RJoOMaGDqYGHJx4FQsa/VvgwSAYDVR0gBEEwPzA9BghggRyG7yoBAjAxMC8GCCsGAQUFBwIBFiNodHRwOi8vd3d3LmNmY2EuY29tLmNuL3VzL3VzLTE1Lmh0bTA6BgNVHR8EMzAxMC+gLaArhilodHRwOi8vMjEwLjc0LjQyLjMvT0NBMTEvUlNBL2NybDI1NzE3LmNybDALBgNVHQ8EBAMCA+gwHQYDVR0OBBYEFMjh6AzDCuNkD+pqQfiS6bqPGpI4MB0GA1UdJQQWMBQGCCsGAQUFBwMCBggrBgEFBQcDBDANBgkqhkiG9w0BAQUFAAOCAQEApZaLXS6/6FudPA3l2xom5U7nJUOpQ1E6DO/ic9dFGtLE0WgyAqB3JVPvXEntppX55x/dAV7rvvz9NaEdiAe8DAj7qyoPDvC8ZWQK8U4n9l8N78QwALOURxzQNs+CBatJQzbu2w1RKVwkfE6xEIVnu+wjiAtymfwdLHMagHxDIC/eOPbTnbbtITJk2ukFfoc0WJ6Awg5lW+x7nGokEn/XAjKyRHCpkRUFGQm4ww41zlrqCqQqnVGVABJtjbdtFf7nh33QHl0fkj19nfMox9eGuntPyM0bNA0XqPMA+FWSCqeDT6uLbyaOKWxlhv53U/NCJl76U3tssMEWsm9amEDDQg==";

        parser.parse(userCert, issuerCert);
        assertEquals("SHA1WITHRSA", parser.getSigAlgName());
        assertEquals(4096, parser.getKeyLength());
    }

    @Test
    public void parseSM3WITHSM2Test() {
        CertParser parser = new CertParser();
        String issuerCert =
                "-----BEGIN CERTIFICATE-----\n" +
                        "MIICTzCCAfOgAwIBAgIKJFSZ4SRVDndYUTAMBggqgRzPVQGDdQUAMF0xCzAJBgNV\n" +
                        "BAYTAkNOMTAwLgYDVQQKDCdDaGluYSBGaW5hbmNpYWwgQ2VydGlmaWNhdGlvbiBB\n" +
                        "dXRob3JpdHkxHDAaBgNVBAMME0NGQ0EgVEVTVCBDUyBTTTIgQ0EwHhcNMTIwODI5\n" +
                        "MDU0ODQ3WhcNMzIwODI0MDU0ODQ3WjBdMQswCQYDVQQGEwJDTjEwMC4GA1UECgwn\n" +
                        "Q2hpbmEgRmluYW5jaWFsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MRwwGgYDVQQD\n" +
                        "DBNDRkNBIFRFU1QgU00yIE9DQTExMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE\n" +
                        "L1mx4wriQUojGsIkNL14kslv9nwiqsiVpELOZauzghrbccNlPYKNYKZOCvXwIIqU\n" +
                        "9QY02d4weqKqo/JMcNsKEaOBmDCBlTAfBgNVHSMEGDAWgBS12JBvXPDYM9JjvX6y\n" +
                        "w43GTxJ6YTAMBgNVHRMEBTADAQH/MDgGA1UdHwQxMC8wLaAroCmGJ2h0dHA6Ly8y\n" +
                        "MTAuNzQuNDIuMy90ZXN0cmNhL1NNMi9jcmwxLmNybDALBgNVHQ8EBAMCAQYwHQYD\n" +
                        "VR0OBBYEFL6mfk09fI+gVebBLwkuLCBDs0J/MAwGCCqBHM9VAYN1BQADSAAwRQIh\n" +
                        "AKuk7s3eYCZDck5NWU0eNQmLhBN/1zmKs517qFrDrkJWAiAP4cVfLtdza/OkwU9P\n" +
                        "PrIDl+E4aL3FypntFXHG3T+Keg==\n" +
                        "-----END CERTIFICATE-----";

        String userCert = "MIICwDCCAmWgAwIBAgIFICdWkWgwDAYIKoEcz1UBg3UFADBdMQswCQYDVQQGEwJDTjEwMC4GA1UECgwnQ2hpbmEgRmluYW5jaWFsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MRwwGgYDVQQDDBNDRkNBIFRFU1QgU00yIE9DQTExMB4XDTE5MDUxNjA4MTA1MVoXDTIxMDUxNjA4MTA1MVoweDELMAkGA1UEBhMCQ04xGDAWBgNVBAoMD0NGQ0EgVEVTVCBPQ0ExMTERMA8GA1UECwwITG9jYWwgUkExFTATBgNVBAsMDEluZGl2aWR1YWwtMTElMCMGA1UEAwwcMDUxQHpoYW5nbGluIUBaMTg2MTIyMjkyOTVAMzBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABPvNXpdZ4/4g+wx5qKS94CPkMqpEDhlnXYYW7ZzsbNI4d28sVBz5Ji6dTT1Zx627Kvw4tdUaUt7BVMvZsu3BFlyjgfQwgfEwHwYDVR0jBBgwFoAUvqZ+TT18j6BV5sEvCS4sIEOzQn8wSAYDVR0gBEEwPzA9BghggRyG7yoBAjAxMC8GCCsGAQUFBwIBFiNodHRwOi8vd3d3LmNmY2EuY29tLmNuL3VzL3VzLTE1Lmh0bTA5BgNVHR8EMjAwMC6gLKAqhihodHRwOi8vMjEwLjc0LjQyLjMvT0NBMTEvU00yL2NybDIxMDkuY3JsMAsGA1UdDwQEAwID6DAdBgNVHQ4EFgQUxR5C/VjASus5zrAAFS4ulMpRjKgwHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMEMAwGCCqBHM9VAYN1BQADRwAwRAIgVBzVi/fgkknr+2BH2wXeGMXC+Pa6p7rbldUsYMOYoyUCIAmQ4KEk2U1xJZSBpOPy5jN9kmRb+0YH6x04O/2tqCgq";

        parser.parse(userCert, issuerCert);
        assertEquals("SM3WITHSM2", parser.getSigAlgName());
        assertEquals(256, parser.getKeyLength());
    }

    @Test
    public void authenticateIssuerByCATest() {
        CertParser parser = new CertParser();
        String CACert =
                "-----BEGIN CERTIFICATE-----\n" +
                        "MIICFDCCAbegAwIBAgIKPYozwkCO86Nd9TAMBggqgRzPVQGDdQUAMF0xCzAJBgNV\n" +
                        "BAYTAkNOMTAwLgYDVQQKDCdDaGluYSBGaW5hbmNpYWwgQ2VydGlmaWNhdGlvbiBB\n" +
                        "dXRob3JpdHkxHDAaBgNVBAMME0NGQ0EgVEVTVCBDUyBTTTIgQ0EwHhcNMTIwODI5\n" +
                        "MDMyOTQ2WhcNMzIwODI5MDMyOTQ2WjBdMQswCQYDVQQGEwJDTjEwMC4GA1UECgwn\n" +
                        "Q2hpbmEgRmluYW5jaWFsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MRwwGgYDVQQD\n" +
                        "DBNDRkNBIFRFU1QgQ1MgU00yIENBMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE\n" +
                        "tTjB3O4JueYFDDOtxH678HBZbEmrsgd3BDIdGf0BekyA26n9S0/pKPnjBh/zLouS\n" +
                        "8+GB5EEnjbn4An24yo1Gv6NdMFswHwYDVR0jBBgwFoAUtdiQb1zw2DPSY71+ssON\n" +
                        "xk8SemEwDAYDVR0TBAUwAwEB/zALBgNVHQ8EBAMCAQYwHQYDVR0OBBYEFLXYkG9c\n" +
                        "8Ngz0mO9frLDjcZPEnphMAwGCCqBHM9VAYN1BQADSQAwRgIhAKwuuqoBS1bwDowW\n" +
                        "a4IU//UsvudswJYSlltqrd/PQ9q+AiEAyTUAjFdaGI+8yPdr3A93UiA38wtGPf9e\n" +
                        "6B6O/6abyWE=\n" +
                        "-----END CERTIFICATE-----";

        String issuerCert = "-----BEGIN CERTIFICATE-----\n" +
                "MIICTzCCAfOgAwIBAgIKJFSZ4SRVDndYUTAMBggqgRzPVQGDdQUAMF0xCzAJBgNV\n" +
                "BAYTAkNOMTAwLgYDVQQKDCdDaGluYSBGaW5hbmNpYWwgQ2VydGlmaWNhdGlvbiBB\n" +
                "dXRob3JpdHkxHDAaBgNVBAMME0NGQ0EgVEVTVCBDUyBTTTIgQ0EwHhcNMTIwODI5\n" +
                "MDU0ODQ3WhcNMzIwODI0MDU0ODQ3WjBdMQswCQYDVQQGEwJDTjEwMC4GA1UECgwn\n" +
                "Q2hpbmEgRmluYW5jaWFsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MRwwGgYDVQQD\n" +
                "DBNDRkNBIFRFU1QgU00yIE9DQTExMFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAE\n" +
                "L1mx4wriQUojGsIkNL14kslv9nwiqsiVpELOZauzghrbccNlPYKNYKZOCvXwIIqU\n" +
                "9QY02d4weqKqo/JMcNsKEaOBmDCBlTAfBgNVHSMEGDAWgBS12JBvXPDYM9JjvX6y\n" +
                "w43GTxJ6YTAMBgNVHRMEBTADAQH/MDgGA1UdHwQxMC8wLaAroCmGJ2h0dHA6Ly8y\n" +
                "MTAuNzQuNDIuMy90ZXN0cmNhL1NNMi9jcmwxLmNybDALBgNVHQ8EBAMCAQYwHQYD\n" +
                "VR0OBBYEFL6mfk09fI+gVebBLwkuLCBDs0J/MAwGCCqBHM9VAYN1BQADSAAwRQIh\n" +
                "AKuk7s3eYCZDck5NWU0eNQmLhBN/1zmKs517qFrDrkJWAiAP4cVfLtdza/OkwU9P\n" +
                "PrIDl+E4aL3FypntFXHG3T+Keg==\n" +
                "-----END CERTIFICATE-----";
        parser.parse(issuerCert, CACert);
        assertEquals("SM3WITHSM2", parser.getSigAlgName());
        assertEquals(256, parser.getKeyLength());
    }
}
