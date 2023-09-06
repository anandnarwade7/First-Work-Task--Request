package com.fin.model.request;


public enum Status {
   NEW, IN_PROGRESS, COMPLETED, REJECTED
}

//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//
//@JsonSerialize(using = Status.StatusSerializer.class)
//public enum Status {
//    NEW, IN_PROGRESS, COMPLETED, REJECTED;
//
//    static class StatusSerializer extends JsonSerializer<Status> {
//        @Override
//        public void serialize(Status value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//            gen.writeString(value.toString()); // Serialize the enum as a string
//        }
//    }
//}

