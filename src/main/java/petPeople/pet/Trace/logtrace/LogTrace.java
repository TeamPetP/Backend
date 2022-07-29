package petPeople.pet.Trace.logtrace;

import petPeople.pet.Trace.TraceStatus;

public interface LogTrace {

    TraceStatus begin(String message);
    void end(TraceStatus status);
    void exception(TraceStatus status, Exception e);
}
