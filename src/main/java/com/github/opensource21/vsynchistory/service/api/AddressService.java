package com.github.opensource21.vsynchistory.service.api;

import java.io.IOException;
import java.io.InputStream;

import com.github.opensource21.vsynchistory.model.DiffResult;

public interface AddressService {

    DiffResult compare(InputStream oldAdressbook, InputStream newAdressbook) throws IOException;

}
