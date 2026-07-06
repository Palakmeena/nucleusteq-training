import React from 'react';
import { Card as MuiCard, CardContent, CardActions, CardHeader } from '@mui/material';
import { Box } from '@mui/material';

const Card = ({
  children,
  title,
  subheader,
  action,
  sx,
  contentSx,
  ...props
}) => {
  return (
    <MuiCard
      sx={{
        borderRadius: 3,
        boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
        ...sx,
      }}
      {...props}
    >
      {(title || subheader || action) && (
        <CardHeader
          title={title}
          subheader={subheader}
          action={action}
          sx={{
            pb: 2,
            '& .MuiCardHeader-title': {
              fontWeight: 600,
              fontSize: '1.25rem',
            },
          }}
        />
      )}
      <CardContent sx={contentSx}>{children}</CardContent>
      {props.cardActions && (
        <CardActions>{props.cardActions}</CardActions>
      )}
    </MuiCard>
  );
};

export default Card;
