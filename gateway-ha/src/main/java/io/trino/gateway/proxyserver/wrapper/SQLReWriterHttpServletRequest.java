package io.trino.gateway.proxyserver.wrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class SQLReWriterHttpServletRequest
        extends HttpServletRequestWrapper
{
    private byte[] content;
    public SQLReWriterHttpServletRequest(HttpServletRequest request)
            throws IOException
    {
        super(request);
        ByteArrayOutputStream bodyInOutputStream = new ByteArrayOutputStream();
        copy(request.getInputStream(), bodyInOutputStream);
        content = bodyInOutputStream.toByteArray();

    }
    static void copy(InputStream in, OutputStream out)
            throws IOException
    {
        byte[] buffer = new byte[1024];
        while (true) {
            int bytesRead = in.read(buffer);
            if (bytesRead == -1) {
                break;
            }
            out.write(buffer, 0, bytesRead);
        }
    }
    @Override
    public ServletInputStream getInputStream()
            throws IOException
    {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
        return new ServletInputStream()
        {
            @Override
            public boolean isFinished()
            {
                return false;
            }

            @Override
            public boolean isReady()
            {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {}

            @Override
            public int read()
                    throws IOException
            {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader()
            throws IOException
    {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}
