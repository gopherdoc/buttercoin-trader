package gopherdoc.buttercoin.trader.Buttercoin;

public class ObjExceptions {
    public String status;
    public int intcode;
    public String msg;

    public ObjExceptions(int intcode){
        this.intcode = intcode;
        parseIntCode();
    }
    private void parseIntCode(){
        switch (this.intcode){
            case 200:
                this.status = "OK";
                this.msg = "OK";
                break;
            case 201:
                this.status = "OK";
                this.msg = "This operation requires e-mail confirmation";
                break;
            case 202:
                this.status = "OK";
                this.msg = "Operation Completed Successfully";
                break;
            case 204:
                this.status = "OK";
                this.msg = "Operation Completed Successfully";
                break;
            case 400:
                this.status = "Error";
                this.msg = "Bad Request";
                break;
            case 401:
                this.status = "Error";
                this.msg = "Unauthorized";
                break;
            case 403:
                this.status = "Error";
                this.msg = "Forbidden";
                break;
            case 404:
                this.status = "Error";
                this.msg = "Not Found";
                break;
            case 405:
                this.status = "Error";
                this.msg = "Invalid Method";
                break;
            case 406:
                this.status = "Error";
                this.msg = "Bad JSON Request";
                break;
            case 415:
                this.status = "Error";
                this.msg = "Unexpected Content Type";
                break;
            case 429:
                this.status = "Error";
                this.msg = "Too Many Requests";
                break;
            case 500:
                this.status = "Error";
                this.msg = "Internal Server Error";
                break;
            case 503:
                this.status = "Error";
                this.msg = "Service Unavailable";
                break;
            default:
                this.status = "Unknown";
                this.msg = "Unknown Response";
                break;

        }
    }
}
