import React from 'react';
import { Box } from '@mui/material';
import { useForm } from 'react-hook-form';

const Form = ({ children, onSubmit, defaultValues, resolver }) => {
  const methods = useForm({
    defaultValues,
    resolver,
  });

  return (
    <Box
      component="form"
      onSubmit={methods.handleSubmit(onSubmit)}
      sx={{ width: '100%' }}
    >
      {typeof children === 'function' ? children(methods) : children}
    </Box>
  );
};

export default Form;
