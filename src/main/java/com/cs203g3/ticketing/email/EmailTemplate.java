package com.cs203g3.ticketing.email;

public final class EmailTemplate {
    public static final String PURCHASE_CONFIRMATION_TITLE = "Purchase Confirmation";
    public static final String PURCHASE_CONFIRMATION = "Dear %s,\n\n"
            + "Thank you for purchasing the ticket(s) for the event: %s.\n\n"
            + "Please find the attached ticket and receipt for your purchase.\n\n"
            + "Regards,\n"
            + "Ticketing Winners";
    
    public static final String BALLOT_SUCCESS_TITLE = "RE: Ballot Selection Result";
    public static final String BALLOT_SUCCESS = "Dear %s,\n\n"
            + "Thank you for participating in the ballot for the event: %s.\n\n"
            + "We are glad to inform you that you have been chosen to buy the tickets! Click on the link below to select your desired seats\n"
            + "%s\n\n"
            + "Regards,\n"
            + "Ticketing Winners";

    public static final String BALLOT_FAILED_TITLE = "RE: Ballot Selection Result";
    public static final String BALLOT_FAILED = "Dear %s,\n\n"
            + "Thank you for participating in the ballot for the event: %s.\n\n"
            + "We are sorry to inform you that you have not been chosen to buy the tickets.\n\n"
            + "Regards,\n"
            + "Ticketing Winners";
}
