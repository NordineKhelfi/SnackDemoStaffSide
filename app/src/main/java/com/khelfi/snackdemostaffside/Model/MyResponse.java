package com.khelfi.snackdemostaffside.Model;

import java.util.List;

/**
 * Downstream HTTP message response body (JSON) model,
 * --> https://firebase.google.com/docs/cloud-messaging/http-server-ref#interpret-downstream
 *
 * Created by norma on 23/01/2018.
 */

public class MyResponse {
    public long multicast_id;
    public int success;
    public int failure;
    public int canonical_ids 	;
    public List<Result> results;
}
